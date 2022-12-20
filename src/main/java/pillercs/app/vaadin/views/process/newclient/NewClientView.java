package pillercs.app.vaadin.views.process.newclient;

import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import pillercs.app.vaadin.data.entity.Applicant;
import pillercs.app.vaadin.data.entity.Application;
import pillercs.app.vaadin.data.enums.Role;
import pillercs.app.vaadin.data.repository.ApplicantRepository;
import pillercs.app.vaadin.data.service.ApplicationService;
import pillercs.app.vaadin.services.WorkflowService;
import pillercs.app.vaadin.views.MainLayout;
import pillercs.app.vaadin.views.process.newclient.components.NewClientForm;

@PageTitle("Choosing the client")
@Route(value = "new-client", layout = MainLayout.class)
public class NewClientView extends VerticalLayout {

    private final NewClientForm newClientForm;
    private final ApplicantRepository applicantRepository;
    private final ApplicationService applicationService;
    private final WorkflowService workflowService;

    public NewClientView(NewClientForm newClientForm,
                         ApplicantRepository applicantRepository,
                         ApplicationService applicationService,
                         WorkflowService workflowService) {
        this.newClientForm = newClientForm;
        this.applicantRepository = applicantRepository;
        this.applicationService = applicationService;
        this.workflowService = workflowService;

        addClassName("new-client-view");
        setWidth("95%");

        final H1 title = new H1("Give us some basic information about the client");

        configureListeners();

        add(title, newClientForm);
    }

    private void configureListeners() {
        newClientForm.addListener(NewClientForm.AddClientEvent.class, e -> {
            Application application = applicationService.getNewApplication();

            Applicant applicant = Applicant.builder()
                    .application(application)
                    .client(e.getClient())
                    .role(Role.DEBTOR)
                    .build();
            applicantRepository.save(applicant);

            workflowService.nextStep(this, application.getApplicationId());
        });
    }

}
