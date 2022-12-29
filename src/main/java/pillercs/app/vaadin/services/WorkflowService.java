package pillercs.app.vaadin.services;

import com.vaadin.flow.component.Component;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pillercs.app.vaadin.data.entity.Applicant;
import pillercs.app.vaadin.data.entity.Application;
import pillercs.app.vaadin.data.entity.Underwriting;
import pillercs.app.vaadin.data.enums.ApplicationState;
import pillercs.app.vaadin.data.repository.ApplicantRepository;
import pillercs.app.vaadin.data.repository.ApplicationRepository;
import pillercs.app.vaadin.data.repository.UnderwritingRepository;
import pillercs.app.vaadin.services.underwriting.UnderwritingResult;
import pillercs.app.vaadin.views.Utils;
import pillercs.app.vaadin.views.process.applicantdetails.ApplicantDetailsView;
import pillercs.app.vaadin.views.process.applicationbasic.ApplicationBasicView;
import pillercs.app.vaadin.views.process.approved.ApprovedView;
import pillercs.app.vaadin.views.process.contract.ContractView;
import pillercs.app.vaadin.views.process.declined.DeclinedView;
import pillercs.app.vaadin.views.process.offers.OffersView;
import pillercs.app.vaadin.views.process.recordincome.IncomeView;
import pillercs.app.vaadin.views.process.selectclient.SelectClientView;
import pillercs.app.vaadin.views.process.underwriting.UnderwritingInProgressView;
import pillercs.app.vaadin.views.process.underwritingresults.UnderwritingResultsView;

import javax.persistence.EntityNotFoundException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import static pillercs.app.vaadin.data.enums.ApplicationState.*;

@Service
@Slf4j
public class WorkflowService {

    private final ApplicationRepository applicationRepository;
    private final Map<ApplicationState, WorkflowStep> workflowSteps = new HashMap<>();

    public WorkflowService(ApplicationRepository applicationRepository,
                           ApplicantRepository applicantRepository,
                           UnderwritingRepository underwritingRepository) {
        this.applicationRepository = applicationRepository;

        registerStep(CREATED,
                (Component source, Application application) -> source.getUI().flatMap(ui -> ui.navigate(SelectClientView.class)),
                application -> {
                    application.setState(RECORD_BASIC_INFORMATION);
                    applicationRepository.save(application);
                });

        registerStep(RECORD_BASIC_INFORMATION,
                (Component source, Application application) -> source.getUI().flatMap(ui -> ui.navigate(ApplicationBasicView.class))
                        .ifPresent(view -> view.setApplicationId(application.getApplicationId())),
                application -> {
                    application.setState(RECORD_INCOME);
                    applicationRepository.save(application);
                });

        registerStep(RECORD_INCOME,
                (Component source, Application application) ->
                        source.getUI().flatMap(ui -> ui.navigate(IncomeView.class))
                                .ifPresent(view -> view.setApplicationId(application.getApplicationId())),
                application -> {
                    application.setState(RECORD_APPLICANT_DETAILS);
                    applicationRepository.save(application);
                });

        registerStep(RECORD_APPLICANT_DETAILS,
                (Component source, Application application) -> {
                    Applicant applicant = applicantRepository.findByApplication_ApplicationId(application.getApplicationId())
                            .orElseThrow(EntityNotFoundException::new);
                    source.getUI().flatMap(ui -> ui.navigate(ApplicantDetailsView.class))
                            .ifPresent(view -> view.setApplicant(applicant));
                },
                application -> {
                    application.setState(UNDERWRITING_IN_PROGRESS);
                    applicationRepository.save(application);
                });

        registerStep(UNDERWRITING_IN_PROGRESS,
                (Component source, Application application) ->
                        source.getUI().flatMap(ui -> ui.navigate(UnderwritingInProgressView.class))
                                .ifPresent(view -> view.startUnderwriting(application.getApplicationId())),
                application -> {
                    application.setState(UNDERWRITING_RESULTS);
                    applicationRepository.save(application);
                });

        registerStep(UNDERWRITING_RESULTS,
                (Component source, Application application) ->
                        source.getUI().ifPresent(ui -> ui.access(() -> ui.navigate(UnderwritingResultsView.class)
                                .ifPresent(view -> view.processResults(application.getApplicationId())))),
                application -> {
                    Underwriting latestUnderwriting = underwritingRepository.findLatestByApplicationId(application.getApplicationId())
                            .orElseThrow();

                    if (latestUnderwriting.getResult() == UnderwritingResult.APPROVED) {
                        application.setState(OFFERS);
                        applicationRepository.save(application);
                    } else {
                        application.setState(DECLINED);
                        applicationRepository.save(application);
                    }
                });

        registerStep(OFFERS,
                (Component source, Application application) ->
                        source.getUI().flatMap(ui -> ui.navigate(OffersView.class))
                                .ifPresent(view -> {
                                    view.setApplicationId(application.getApplicationId());
                                    view.showOffers();
                                }),
                application -> {
                    application.setState(CONTRACT);
                    applicationRepository.save(application);
                });

        registerStep(CONTRACT,
                (Component source, Application application) ->
                        source.getUI().flatMap(ui -> ui.navigate(ContractView.class))
                                .ifPresent(view -> {
                                    view.setApplicationId(application.getApplicationId());
                                }),
                application -> {
                    application.setState(APPROVED);
                    applicationRepository.save(application);
                });

        registerStep(APPROVED,
                (Component source, Application application) ->
                        source.getUI().ifPresent(ui -> ui.navigate(ApprovedView.class)),
                application -> {});

        registerStep(DECLINED,
                (Component source, Application application) ->
                        source.getUI().ifPresent(ui -> ui.navigate(DeclinedView.class)),
                application -> {});
    }


    private void registerStep(ApplicationState currentState, BiConsumer<Component, Application> toCurrentStep, Consumer<Application> toNextState) {
        workflowSteps.put(currentState, new WorkflowStep(currentState, toCurrentStep, toNextState));
    }

    public void nextStep(Component source, Long applicationId) {
        Application application = getApplication(applicationId);

        WorkflowStep wfStep = getWfStep(application, source);

        wfStep.getToNextState().accept(application);
        wfStep = getWfStep(application, source);
        wfStep.getToCurrentStep().accept(source, application);
    }

    public void currentStep(Component source, Long applicationId) {
        Application application = getApplication(applicationId);

        ApplicationState currentState = getCurrentState(application, source);

        workflowSteps.get(currentState).getToCurrentStep().accept(source, application);
    }

    private Application getApplication(long applicationId) {
        return applicationRepository.findById(applicationId).orElseThrow(EntityNotFoundException::new);
    }

    private WorkflowStep getWfStep(Application application, Component source) {
        ApplicationState currentState = getCurrentState(application, source);

        WorkflowStep wfStep = workflowSteps.get(currentState);

        if (wfStep == null) {
            log.error("Couldn't navigate to next step, state {} is not registered", currentState);
            Utils.routeHome(source);
            return null;
        }

        return wfStep;
    }

    private ApplicationState getCurrentState(Application application, Component source) {
        ApplicationState currentState = application.getState();

        if (currentState == null) {
            log.error("Couldn't navigate to next step, application: {} doesn't have a state", application.getApplicationId());
            Utils.routeHome(source);
            return null;
        }

        return currentState;
    }

    @Getter
    private class WorkflowStep {

        private final ApplicationState currentState;
        private final BiConsumer<Component, Application> toCurrentStep;
        private final Consumer<Application> toNextState;

        WorkflowStep(ApplicationState currentState, BiConsumer<Component, Application> toCurrentStep, Consumer<Application> toNextState) {
            this.currentState = currentState;
            this.toCurrentStep = toCurrentStep;
            this.toNextState = toNextState;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof WorkflowStep that)) return false;
            return currentState == that.currentState;
        }

        @Override
        public int hashCode() {
            return Objects.hash(currentState);
        }
    }
}
