package pillercs.app.vaadin.views.process.applicantdetails;

import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import pillercs.app.vaadin.data.entity.Applicant;
import pillercs.app.vaadin.data.enums.MaritalStatus;
import pillercs.app.vaadin.data.enums.SchoolLevel;
import pillercs.app.vaadin.data.repository.ApplicantRepository;
import pillercs.app.vaadin.services.WorkflowService;
import pillercs.app.vaadin.views.MainLayout;

@PageTitle("Applicant details")
@Route(value = "applicant-details", layout = MainLayout.class)
@Slf4j
public class ApplicantDetailsView extends VerticalLayout {

    private final ApplicantRepository applicantRepository;
    private final WorkflowService workflowService;

    private final Binder<Applicant> binder = new Binder<>(Applicant.class);

    private final ComboBox<SchoolLevel> highestLevelOfSchool = new ComboBox<>("School level status");
    private final ComboBox<MaritalStatus> maritalStatus = new ComboBox<>("Marital status");
    private final IntegerField householdNumber = new IntegerField("Household number");
    private final IntegerField numberOfDependants = new IntegerField("Number of dependants");
    private final IntegerField outstandingLoansInstalment = new IntegerField("Outstanding loans instalment");

    @Setter
    private Applicant applicant;

    public ApplicantDetailsView(ApplicantRepository applicantRepository, WorkflowService workflowService) {
        this.applicantRepository = applicantRepository;
        this.workflowService = workflowService;

        addClassName(LumoUtility.Padding.MEDIUM);
        addClassName("applicant-details-view");
        setWidth("95%");

        binder.forField(numberOfDependants)
                .asRequired()
                .withValidator(value -> value < householdNumber.getValue(),
                    "The number of dependants must be lower than household")
                .withValidator(value -> value >= 0, "Can't be negative")
                .bind("numberOfDependants");

        binder.bindInstanceFields(this);

        highestLevelOfSchool.setItems(SchoolLevel.values());
        highestLevelOfSchool.setItemLabelGenerator(SchoolLevel::getName);
        highestLevelOfSchool.setRequiredIndicatorVisible(true);

        maritalStatus.setItems(MaritalStatus.values());
        maritalStatus.setItemLabelGenerator(MaritalStatus::getName);
        maritalStatus.setRequiredIndicatorVisible(true);

        householdNumber.setRequiredIndicatorVisible(true);
        outstandingLoansInstalment.setPrefixComponent(new Div(new Text("HUF")));
        outstandingLoansInstalment.setRequiredIndicatorVisible(true);

        FormLayout formLayout = new FormLayout(highestLevelOfSchool, maritalStatus, householdNumber, numberOfDependants, outstandingLoansInstalment);

        Button next = createButtons();

        add(new H2("Please fill in more details about the applicant"), formLayout, next);
    }

    private Button createButtons() {
        Button next = new Button("Next");
        next.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        next.addClickShortcut(Key.ENTER);
        next.setEnabled(false);

        binder.addValueChangeListener(e -> next.setEnabled(binder.isValid()));

        next.addClickListener(c -> {
            if (applicant == null) {
                Notification.show("Something went wrong").setDuration(3_000);
                log.error("An applicant has not been set on ApplicantDetailsView");
            }

            if (binder.writeBeanIfValid(applicant)) {
                applicantRepository.save(applicant);

                workflowService.nextStep(this, applicant.getApplication().getApplicationId());
            }
        });

        return next;
    }
}
