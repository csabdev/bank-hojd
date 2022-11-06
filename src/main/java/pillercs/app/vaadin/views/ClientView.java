package pillercs.app.vaadin.views;

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
import pillercs.app.vaadin.views.components.NewClientForm;
import pillercs.app.vaadin.views.components.SingleSelectClientGrid;

@PageTitle("Choosing the client")
@Route(value = "client", layout = MainLayout.class)
public class ClientView extends VerticalLayout {

    private final ApplicantRepository applicantRepository;

    public ClientView(SingleSelectClientGrid singleSelectClientGrid,
                      NewClientForm newClientForm,
                      ApplicantRepository applicantRepository) {
        this.applicantRepository = applicantRepository;
        singleSelectClientGrid.addListener(SingleSelectClientGrid.SelectEvent.class, event -> {
            System.out.println(event.getClient());
            createApplicationAndRoute(event.getClient());
        });

        final H1 h1 = new H1("Please choose the client who is applying for the loan!");

        final H2 h2 = new H2("Didn't find the client you were looking for?");

        newClientForm.addListener(NewClientForm.AddClientEvent.class, event -> {
            System.out.println(event.getClient());
            createApplicationAndRoute(event.getClient());
        });

        add(h1, singleSelectClientGrid, h2, newClientForm);
    }

    private void createApplicationAndRoute(Client client) {
        Applicant debtor = Applicant.builder()
                .role(Role.DEBTOR)
                .application(Application.builder().createdByUser("test").build())
                .build();

        Applicant savedDebtor = applicantRepository.save(debtor);
        Application savedApplication = savedDebtor.getApplication();

        savedDebtor.setClient(client);
        applicantRepository.save(savedDebtor);

        this.getUI().flatMap(ui -> ui.navigate(ApplicationBasicView.class))
                .ifPresent(view -> view.setApplication(savedApplication));
    }

}
