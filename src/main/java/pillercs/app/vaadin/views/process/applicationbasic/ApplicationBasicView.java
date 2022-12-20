package pillercs.app.vaadin.views.process.applicationbasic;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import pillercs.app.vaadin.components.FormattedIntegerLayout;
import pillercs.app.vaadin.data.entity.Application;
import pillercs.app.vaadin.data.entity.CashLoanProduct;
import pillercs.app.vaadin.data.repository.ApplicationRepository;
import pillercs.app.vaadin.data.repository.CashLoanProductRepository;
import pillercs.app.vaadin.services.WorkflowService;
import pillercs.app.vaadin.views.MainLayout;
import pillercs.app.vaadin.views.Utils;

@Setter
@Slf4j
@PageTitle("Basic information about the application")
@Route(value = "basic-application", layout = MainLayout.class)
public class ApplicationBasicView extends VerticalLayout {

    private final CashLoanProductRepository cashLoanProductRepository;
    private final ApplicationRepository applicationRepository;
    private final WorkflowService workflowService;

    private final FormattedIntegerLayout loanAmount;
    private final FormattedIntegerLayout term;
    private final FormattedIntegerLayout monthlyInstalment = new FormattedIntegerLayout("Monthly instalment", 0);
    private final Button next;

    private Long applicationId;

    private static final int AMOUNT_STEP = 100000;
    private static final int AMOUNT_DEFAULT = 2_400_000;
    private static final int TERM_STEP = 12;
    private static final int TERM_DEFAULT = 36;
    private static final String FIELD_WIDTH = "320px";

    public ApplicationBasicView(CashLoanProductRepository cashLoanProductRepository,
                                ApplicationRepository applicationRepository,
                                WorkflowService workflowService) {
        this.cashLoanProductRepository = cashLoanProductRepository;
        this.applicationRepository = applicationRepository;
        this.workflowService = workflowService;

        addClassName("application-basic-view");
        setWidth("95%");

        CashLoanProduct activeProduct = this.cashLoanProductRepository.findCashLoanProductByValidToIsNull().orElseThrow();

        final String amountHelperText = String.format("The loan amount has to be between %s %,d and %,d", activeProduct.getCurrency(), activeProduct.getMinAmount(), activeProduct.getMaxAmount());
        loanAmount = createIntegerField("Loan amount",
                activeProduct.getMinAmount(),
                activeProduct.getMaxAmount(),
                AMOUNT_STEP,
                AMOUNT_DEFAULT,
                amountHelperText,
                activeProduct);
        loanAmount.setPrefix("HUF");

        final String termHelperText = String.format("The term has to be between %d and %d month", activeProduct.getMinTerm(), activeProduct.getMaxTerm());
        term = createIntegerField("Term",
                activeProduct.getMinTerm(),
                activeProduct.getMaxTerm(),
                TERM_STEP,
                TERM_DEFAULT,
                termHelperText,
                activeProduct);

        monthlyInstalment.setReadOnly(true);
        monthlyInstalment.setPrefix(activeProduct.getCurrency());
        monthlyInstalment.removeControls();
        monthlyInstalment.addClassName("application-basic-input-field");
        monthlyInstalment.setWidth(FIELD_WIDTH);

        next = createNextButton(activeProduct);

        updateMonthlyInstalment(activeProduct);

        H2 instructions = new H2("Please record the requested amount and term");
        add(instructions, loanAmount, term, monthlyInstalment, next);
    }

    private FormattedIntegerLayout createIntegerField(String label, int min, int max, int step, int defaultValue, String helper, CashLoanProduct activeProduct) {
        FormattedIntegerLayout integerField = new FormattedIntegerLayout(label, step);
        integerField.addClassName("application-basic-input-field");

        integerField.setWidth(FIELD_WIDTH);
        integerField.setLimits(min, max);
        integerField.setValue(defaultValue);
        integerField.addValueChangeListener(e -> updateMonthlyInstalment(activeProduct));
        integerField.setHelperText(helper);

        return integerField;
    }

    private void updateMonthlyInstalment(CashLoanProduct activeProduct) {
        int presentValue = loanAmount.getValue();
        int requestedTerm = term.getValue();
        if (!isValid(activeProduct, presentValue, requestedTerm)) {
            monthlyInstalment.setValue(0);
            next.setEnabled(false);
            return;
        }

        double interestRate = activeProduct.getInterestRate() / 12;
        monthlyInstalment.setValue(Utils.calculateMonthlyInstalment(interestRate, presentValue, requestedTerm));
        next.setEnabled(true);
    }

    private boolean isValid(CashLoanProduct activeProduct, Integer presentValue, Integer requestedTerm) {
        return presentValue != null && presentValue <= activeProduct.getMaxAmount() && presentValue >= activeProduct.getMinAmount() &&
                requestedTerm != null && requestedTerm <= activeProduct.getMaxTerm() && requestedTerm >= activeProduct.getMinTerm();
    }

    private Button createNextButton(CashLoanProduct activeProduct) {
        Button next = new Button("Continue");
        next.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        next.addClickListener(e -> {
            if (!isValid(activeProduct, loanAmount.getValue(), term.getValue())) return;
            if (applicationId == null) {
                Notification.show("Something went wrong").setDuration(3_000);
                log.error("An application has not been set on ApplicantDetailsView");
                return;
            }

            Application application = applicationRepository.findById(applicationId).orElseThrow();
            updateMonthlyInstalment(activeProduct);
            application.setLoanAmount(loanAmount.getValue());
            application.setTerm(term.getValue());
            application.setMonthlyInstalment(monthlyInstalment.getValue());
            applicationRepository.save(application);

            workflowService.nextStep(this, applicationId);
        });

        return next;
    }
}
