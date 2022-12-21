package pillercs.app.vaadin.views.process.underwritingresults;

import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.accordion.Accordion;
import com.vaadin.flow.component.accordion.AccordionPanel;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.details.DetailsVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.ListItem;
import com.vaadin.flow.component.html.UnorderedList;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility;
import pillercs.app.vaadin.data.entity.Underwriting;
import pillercs.app.vaadin.data.entity.UnderwritingStepDetailEntity;
import pillercs.app.vaadin.data.entity.UnderwritingStepEntity;
import pillercs.app.vaadin.data.repository.UnderwritingRepository;
import pillercs.app.vaadin.data.repository.UnderwritingStepDetailRepository;
import pillercs.app.vaadin.services.WorkflowService;
import pillercs.app.vaadin.services.underwriting.StepResult;
import pillercs.app.vaadin.services.underwriting.UnderwritingResult;
import pillercs.app.vaadin.views.MainLayout;

import javax.annotation.security.PermitAll;
import java.util.Comparator;
import java.util.List;

@Route(value = "underwriting-results", layout = MainLayout.class)
@PageTitle("Underwriting results")
@PermitAll
public class UnderwritingResultsView extends VerticalLayout {

    private final WorkflowService workflowService;
    private final UnderwritingRepository underwritingRepository;
    private final UnderwritingStepDetailRepository underwritingStepDetailRepository;

    H1 placeholder = new H1("This screen can only be called in the process, " +
            "please continue the application from the Home page");

    public UnderwritingResultsView(WorkflowService workflowService,
                                   UnderwritingRepository underwritingRepository,
                                   UnderwritingStepDetailRepository underwritingStepDetailRepository) {
        this.workflowService = workflowService;
        this.underwritingRepository = underwritingRepository;
        this.underwritingStepDetailRepository = underwritingStepDetailRepository;

        setWidth("95%");
        addClassName("underwriting-results-view");

        add(placeholder);
    }

    public void processResults(long applicationId) {
        remove(placeholder);

        List<Underwriting> underwritings = underwritingRepository.findByApplicationIdWithSteps(applicationId);

        if (!underwritings.isEmpty()) {
            Underwriting latestUnderwriting = underwritings.stream()
                    .max(Comparator.comparing(Underwriting::getUnderwritingId)).get();

            Div resultDiv = new Div();
            resultDiv.addClassName(LumoUtility.FontSize.XXLARGE);
            resultDiv.setSizeFull();

            if (UnderwritingResult.APPROVED == latestUnderwriting.getResult()) {
                resultDiv.add(new Icon(VaadinIcon.CHECK_CIRCLE_O));
                resultDiv.add(new Text("The application has been approved"));
                resultDiv.addClassName("approved");
            } else {
                resultDiv.add(new Icon(VaadinIcon.BAN));
                resultDiv.add(new Text("The application has been declined"));
                resultDiv.addClassName("declined");
            }

            addComponentAtIndex(0, resultDiv);

            final Accordion detailsAccordion = new Accordion();

            for (UnderwritingStepEntity underwritingStep : latestUnderwriting.getSteps()) {
                UnorderedList detailsPanel = new UnorderedList();
                List<UnderwritingStepDetailEntity> underwritingStepDetails =
                        underwritingStepDetailRepository.findAllByUnderwritingStep_UnderwritingStepId(underwritingStep.getUnderwritingStepId());
                for (UnderwritingStepDetailEntity detail : underwritingStepDetails) {
                    ListItem li = new ListItem(detail.getDetail());
                    detailsPanel.add(li);
                }

                final boolean isApproved = underwritingStep.getResult() == StepResult.APPROVED;

                AccordionPanel accordionPanel = detailsAccordion.add(underwritingStep.getStepName(), detailsPanel);
                accordionPanel.addClassName(isApproved ? "approved-step" : "declined-step");
                accordionPanel.addThemeVariants(DetailsVariant.FILLED);
            }

            add(detailsAccordion);

            final Button nextButton = new Button("Next");
            nextButton.addClickListener(c -> workflowService.nextStep(this, applicationId));
            add(nextButton);
        }
    }
}
