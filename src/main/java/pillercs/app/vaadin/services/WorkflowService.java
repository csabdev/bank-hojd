package pillercs.app.vaadin.services;

import com.vaadin.flow.component.Component;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pillercs.app.vaadin.data.entity.Applicant;
import pillercs.app.vaadin.data.entity.Application;
import pillercs.app.vaadin.data.enums.ApplicationState;
import pillercs.app.vaadin.data.repository.ApplicantRepository;
import pillercs.app.vaadin.data.repository.ApplicationRepository;
import pillercs.app.vaadin.views.process.applicantdetails.ApplicantDetailsView;
import pillercs.app.vaadin.views.process.approved.ApprovedView;
import pillercs.app.vaadin.views.process.offers.OffersView;
import pillercs.app.vaadin.views.process.recordincome.IncomeView;
import pillercs.app.vaadin.views.process.underwriting.UnderwritingInProgressView;

import javax.persistence.EntityNotFoundException;

import static pillercs.app.vaadin.data.enums.ApplicationState.*;

@Service
@RequiredArgsConstructor
public class WorkflowService {

    private final ApplicationRepository applicationRepository;
    private final ApplicantRepository applicantRepository;

    public void next(Long applicationId, Component source) {
        Application application = applicationRepository.findById(applicationId).orElseThrow(EntityNotFoundException::new);

        ApplicationState currentState = application.getState();

        switch (currentState) {
            case RECORD_BASIC_INFORMATION:
                source.getUI().flatMap(ui -> ui.navigate(IncomeView.class))
                        .ifPresent(view -> view.setApplicationId(applicationId));
                application.setState(RECORD_INCOME);
                break;
            case RECORD_INCOME:
                Applicant applicant = applicantRepository.findByApplication_ApplicationId(applicationId)
                        .orElseThrow(EntityNotFoundException::new);
                source.getUI().flatMap(ui -> ui.navigate(ApplicantDetailsView.class))
                        .ifPresent(view -> view.setApplicant(applicant));
                application.setState(RECORD_APPLICANT_DETAILS);
                break;
            case RECORD_APPLICANT_DETAILS:
                source.getUI().flatMap(ui -> ui.navigate(UnderwritingInProgressView.class))
                        .ifPresent(view -> view.startUnderwriting(applicationId));
                application.setState(UNDERWRITING_IN_PROGRESS);
                break;
            case UNDERWRITING_IN_PROGRESS:
                source.getUI().ifPresent(ui -> ui.access(() -> ui.navigate(OffersView.class)
                        .ifPresent(view -> {
                            view.setApplicationId(applicationId);
                            view.showOffers();
                        })));
                application.setState(OFFERS);
                break;
            case OFFERS:
                source.getUI().ifPresent(ui -> ui.navigate(ApprovedView.class));
                application.setState(APPROVED);
                break;
            case APPROVED:
                break;
        }

    }
}
