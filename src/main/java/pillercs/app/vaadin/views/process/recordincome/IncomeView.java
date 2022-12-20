package pillercs.app.vaadin.views.process.recordincome;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import lombok.extern.slf4j.Slf4j;
import pillercs.app.vaadin.data.entity.Applicant;
import pillercs.app.vaadin.data.entity.Application;
import pillercs.app.vaadin.data.entity.Employer;
import pillercs.app.vaadin.data.entity.Income;
import pillercs.app.vaadin.data.repository.ApplicationRepository;
import pillercs.app.vaadin.data.repository.EmployerRepository;
import pillercs.app.vaadin.data.repository.IncomeRepository;
import pillercs.app.vaadin.services.WorkflowService;
import pillercs.app.vaadin.views.MainLayout;
import pillercs.app.vaadin.views.process.recordincome.components.EmployerLayout;
import pillercs.app.vaadin.views.process.recordincome.components.IncomeLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@PageTitle("Adding incomes")
@Route(value = "income", layout = MainLayout.class)
@Slf4j
public class IncomeView extends VerticalLayout {

    private final EmployerLayout employerLayout;
    private final IncomeLayout incomeLayout;
    private final IncomeRepository incomeRepository;
    private final ApplicationRepository applicationRepository;
    private final EmployerRepository employerRepository;
    private final WorkflowService workflowService;

    private final VerticalLayout buttons;
    private final Button continueToIncome = new Button("Continue to incomes");
    private final Button nextStep = new Button("Next step");
    private final Button backToEmployers = new Button("Edit employers");
    private final H2 instructions = new H2("Then continue with recording the incomes (this is mandatory)");

    private Long applicationId;
    private List<Employer> employerList = new ArrayList<>();
    private List<Income> incomeList = new ArrayList<>();
    private boolean incomeVisible = false;

    public IncomeView(EmployerLayout employerLayout,
                      IncomeLayout incomeLayout,
                      IncomeRepository incomeRepository,
                      ApplicationRepository applicationRepository,
                      EmployerRepository employerRepository,
                      WorkflowService workflowService) {
        this.employerLayout = employerLayout;
        this.incomeLayout = incomeLayout;
        this.incomeRepository = incomeRepository;
        this.applicationRepository = applicationRepository;
        this.employerRepository = employerRepository;
        this.workflowService = workflowService;

        addClassName("income-view");
        setWidth("95%");

        this.incomeLayout.setVisible(incomeVisible);

        continueToIncome.setWidth("200px");
        buttons = new VerticalLayout(instructions, continueToIncome);
        configureButtons();
        configureListeners();

        add(employerLayout, buttons, incomeLayout, new HorizontalLayout(nextStep, backToEmployers));
    }

    private void configureButtons() {
        nextStep.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        nextStep.setVisible(false);
        nextStep.addClickListener(c -> {
            if (applicationId == null) {
                Notification.show("Something went wrong");
                log.error("Application ID was not set on IncomeView");
                return;
            }

            Application application = applicationRepository.findWithApplicants(applicationId).orElseThrow();
            Applicant applicant = application.getApplicants().stream().findFirst().orElseThrow();

            HashMap<String, Employer> employers = new HashMap<>();

            for (Income income : incomeList) {
                income.setApplicant(applicant);

                Employer unsavedEmployer = income.getEmployer();

                if (unsavedEmployer == null) continue;

                if (employers.containsKey(unsavedEmployer.getName() + unsavedEmployer.getTaxNumber())) {
                    income.setEmployer(employers.get(unsavedEmployer.getName() + unsavedEmployer.getTaxNumber()));
                } else {
                    unsavedEmployer.setApplication(application);
                    Employer savedEmployer = employerRepository.save(unsavedEmployer);
                    income.setEmployer(savedEmployer);
                    employers.put(savedEmployer.getName() + savedEmployer.getTaxNumber(), savedEmployer);
                }
            }

            incomeRepository.saveAll(incomeList);

            workflowService.nextStep(this, applicationId);
        });

        backToEmployers.setVisible(false);
        backToEmployers.addClickListener(c -> toggleLayouts());

        continueToIncome.addClickListener(c -> toggleLayouts());
    }

    private void toggleLayouts() {
        incomeVisible = !incomeVisible;

        incomeLayout.setVisible(incomeVisible);
        nextStep.setVisible(incomeVisible);
        backToEmployers.setVisible(incomeVisible);

        employerLayout.setVisible(!incomeVisible);
        employerLayout.setPadding(!incomeVisible);
        buttons.setPadding(!incomeVisible);
        continueToIncome.setVisible(!incomeVisible);
        instructions.setVisible(!incomeVisible);
    }

    private void configureListeners() {
        employerLayout.addListener(EmployerLayout.EmployerListChangedEvent.class, e -> {
            employerList = e.getEmployerList();
            incomeLayout.setEmployerList(employerList);
        });

        incomeLayout.addListener(IncomeLayout.IncomeListChangedEvent.class, e -> {
            incomeList = e.getIncomeList();
            employerLayout.setIncomeList(incomeList);
        });
    }

    public void setApplicationId(Long applicationId) {
        this.applicationId = applicationId;
    }
}
