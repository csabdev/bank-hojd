package pillercs.app.vaadin.views.process.selectclient;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import pillercs.app.vaadin.data.entity.Applicant;
import pillercs.app.vaadin.data.entity.Application;
import pillercs.app.vaadin.data.entity.Client;
import pillercs.app.vaadin.data.enums.Role;
import pillercs.app.vaadin.data.repository.ApplicantRepository;
import pillercs.app.vaadin.data.service.ApplicationService;
import pillercs.app.vaadin.services.WorkflowService;
import pillercs.app.vaadin.views.MainLayout;
import pillercs.app.vaadin.views.process.newclient.NewClientView;
import pillercs.app.vaadin.views.process.selectclient.components.SingleSelectClientGrid;

import javax.annotation.security.PermitAll;

@PageTitle("Choosing the client")
@Route(value = "select-client", layout = MainLayout.class)
@PermitAll
public class SelectClientView extends VerticalLayout {

    private final ApplicantRepository applicantRepository;
    private final ApplicationService applicationService;
    private final WorkflowService workflowService;

    public SelectClientView(SingleSelectClientGrid singleSelectClientGrid,
                            ApplicantRepository applicantRepository,
                            ApplicationService applicationService,
                            WorkflowService workflowService) {
        this.applicantRepository = applicantRepository;
        this.applicationService = applicationService;
        this.workflowService = workflowService;

        addClassName("select-client-view");
        setWidth("95%");

        singleSelectClientGrid.addListener(SingleSelectClientGrid.SelectEvent.class, event ->
                createApplicationAndRoute(event.getClient()));

        final H1 h1 = new H1("Please choose the client who is applying for the loan!");

        final H2 h2 = new H2("Didn't find the client you were looking for?");

        Button createNewClient = new Button("Create new client");
        createNewClient.addClickListener(c -> this.getUI().flatMap(ui -> ui.navigate(NewClientView.class)));

        add(new VerticalLayout(h1, singleSelectClientGrid), new VerticalLayout(h2, createNewClient));
    }

    private void createApplicationAndRoute(Client client) {
        Application application = applicationService.getNewApplication();

        Applicant debtor = Applicant.builder()
                .role(Role.DEBTOR)
                .application(application)
                .client(client)
                .build();

        applicantRepository.save(debtor);

        workflowService.nextStep(this, application.getApplicationId());
    }

}
