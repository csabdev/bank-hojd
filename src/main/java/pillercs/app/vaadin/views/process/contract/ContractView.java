package pillercs.app.vaadin.views.process.contract;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import lombok.extern.slf4j.Slf4j;
import org.vaadin.olli.FileDownloadWrapper;
import pillercs.app.vaadin.data.entity.Client;
import pillercs.app.vaadin.data.repository.ApplicantRepository;
import pillercs.app.vaadin.data.repository.ApplicationRepository;
import pillercs.app.vaadin.services.PdfService;
import pillercs.app.vaadin.services.WorkflowService;
import pillercs.app.vaadin.views.MainLayout;

import javax.annotation.security.PermitAll;
import java.io.File;

@Route(value = "contract", layout = MainLayout.class)
@PageTitle("Contract")
@PermitAll
@Slf4j
public class ContractView extends VerticalLayout {

    private final PdfService pdfService;
    private final ApplicationRepository applicationRepository;
    private final ApplicantRepository applicantRepository;
    private final WorkflowService workflowService;

    private final Button createPdf;
    private final Button buttonWrapper;

    public ContractView(PdfService pdfService, ApplicationRepository applicationRepository, ApplicantRepository applicantRepository, WorkflowService workflowService) {
        this.pdfService = pdfService;
        this.applicationRepository = applicationRepository;
        this.applicantRepository = applicantRepository;
        this.workflowService = workflowService;

        H1 intro = new H1("Almost done!");
        H2 todo = new H2("Please generate and then sign the contract");

        createPdf = new Button("Create contract");
        buttonWrapper = new Button("Download contract");

        add(intro, todo, createPdf);
    }

    public void setApplicationId(long applicationId) {
        Client client = applicantRepository.findApplicantsByApplicationIdWithClients(applicationId).get(0).getClient();
        String clientName = client.getFirstName() + "_" + client.getLastName();

        FileDownloadWrapper wrapper = new FileDownloadWrapper(clientName + ".pdf", new File(clientName + ".pdf"));
        wrapper.wrapComponent(buttonWrapper);

        createPdf.addClickListener(c -> {
            pdfService.createPdf(applicationId);
            Notification notification = Notification
                    .show("Contract generated!");
            notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
            addComponentAtIndex(3, wrapper);
        });

        Button next = new Button("Contract signed", c -> workflowService.nextStep(this, applicationId));
        next.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        add(next);
    }

}
