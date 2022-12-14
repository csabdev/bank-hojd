package pillercs.app.vaadin.views.process.underwriting;

import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.progressbar.ProgressBar;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.concurrent.ListenableFuture;
import pillercs.app.vaadin.services.underwriting.UnderwritingService;
import pillercs.app.vaadin.services.WorkflowService;
import pillercs.app.vaadin.views.MainLayout;

import javax.annotation.security.PermitAll;

@PageTitle("Underwriting in progress")
@Route(value = "underwriting-in-progress", layout = MainLayout.class)
@Slf4j
@PermitAll
public class UnderwritingInProgressView extends VerticalLayout {

    private final UnderwritingService underwritingService;
    private final WorkflowService workflowService;

    public UnderwritingInProgressView(UnderwritingService underwritingService, WorkflowService workflowService) {
        this.underwritingService = underwritingService;
        this.workflowService = workflowService;

        ProgressBar progressBar = new ProgressBar();
        progressBar.setIndeterminate(true);

        Paragraph progressBarLabel = new Paragraph();
        progressBarLabel.setText("Please wait for the result of automatic underwriting. It shouldn't take long!");

        addClassNames(LumoUtility.Padding.XLARGE, LumoUtility.AlignItems.CENTER);

        add(progressBarLabel, progressBar);
    }

    public void startUnderwriting(Long applicationId) {
        ListenableFuture<Void> underwritingResult = underwritingService.runUnderwriting(applicationId);

        underwritingResult.addCallback(result -> workflowService.nextStep(this, applicationId),
                ex -> {
            log.error("Underwriting failed");
            log.error(ex.getMessage());
            ex.printStackTrace();
        });
    }

}
