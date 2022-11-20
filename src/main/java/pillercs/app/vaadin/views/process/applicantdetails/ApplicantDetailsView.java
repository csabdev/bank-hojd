package pillercs.app.vaadin.views.process.applicantdetails;

import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.notification.Notification;
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
import pillercs.app.vaadin.views.MainLayout;
import pillercs.app.vaadin.views.process.underwriting.UnderwritingInProgressView;

@PageTitle("Applicant details")
@Route(value = "applicant-details", layout = MainLayout.class)
@Slf4j
public class ApplicantDetailsView extends FormLayout {

    private final ApplicantRepository applicantRepository;

    private final Binder<Applicant> binder = new Binder<>(Applicant.class);

    private final ComboBox<SchoolLevel> schoolLevel = new ComboBox<>("School level status");
    private final ComboBox<MaritalStatus> maritalStatus = new ComboBox<>("Marital status");
    private final IntegerField householdNumber = new IntegerField("Household number");
    private final IntegerField numberOfDependants = new IntegerField("Number of dependants");
    private final IntegerField outstandingLoansInstalment = new IntegerField("Outstanding loans instalment");

    @Setter
    private Applicant applicant;

    public ApplicantDetailsView(ApplicantRepository applicantRepository) {
        this.applicantRepository = applicantRepository;

        addClassName(LumoUtility.Padding.MEDIUM);

        binder.forField(numberOfDependants)
                .asRequired()
                .withValidator(value -> value < householdNumber.getValue(),
                    "The number of dependants must be lower than household")
                .withValidator(value -> value >= 0, "Can't be negative")
                .bind("numberOfDependants");

        binder.bindInstanceFields(this);

        schoolLevel.setItems(SchoolLevel.values());
        schoolLevel.setItemLabelGenerator(SchoolLevel::getName);

        maritalStatus.setItems(MaritalStatus.values());
        maritalStatus.setItemLabelGenerator(MaritalStatus::getName);

        Button next = createButtons();

        add(schoolLevel, maritalStatus, householdNumber, numberOfDependants, outstandingLoansInstalment, next);
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

                this.getUI().flatMap(ui -> ui.navigate(UnderwritingInProgressView.class))
                        .ifPresent(view -> view.startUnderwriting(applicant.getApplication().getApplicationId()));
            }
        });

        return next;
    }
}
