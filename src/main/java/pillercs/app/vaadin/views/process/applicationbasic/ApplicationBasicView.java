package pillercs.app.vaadin.views.process.applicationbasic;

import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import lombok.Setter;
import pillercs.app.vaadin.data.entity.Application;
import pillercs.app.vaadin.data.entity.CashLoanProduct;
import pillercs.app.vaadin.data.repository.ApplicationRepository;
import pillercs.app.vaadin.data.repository.CashLoanProductRepository;
import pillercs.app.vaadin.views.MainLayout;
import pillercs.app.vaadin.views.Utils;
import pillercs.app.vaadin.views.process.recordincome.IncomeView;

@Setter
@PageTitle("Basic information about the application")
@Route(value = "basic-application", layout = MainLayout.class)
public class ApplicationBasicView extends VerticalLayout {

    private final CashLoanProductRepository cashLoanProductRepository;
    private final ApplicationRepository applicationRepository;

    private final IntegerField loanAmount;
    private final IntegerField term;
    private final IntegerField monthlyInstalment = new IntegerField("Monthly instalment");
    private final Button next;

    private Long applicationId;

    private static final int AMOUNT_STEP = 10_000;
    private static final int AMOUNT_DEFAULT = 2_400_000;
    private static final int TERM_STEP = 12;
    private static final int TERM_DEFAULT = 36;
    private static final String FIELD_WIDTH = "250px";

    public ApplicationBasicView(CashLoanProductRepository cashLoanProductRepository, ApplicationRepository applicationRepository) {
        this.cashLoanProductRepository = cashLoanProductRepository;
        this.applicationRepository = applicationRepository;

        setSizeFull();

        CashLoanProduct activeProduct = this.cashLoanProductRepository.findCashLoanProductByValidToIsNull().orElseThrow();

        final String amountHelperText = String.format("The loan amount has to be between %s %,d and %,d", activeProduct.getCurrency(), activeProduct.getMinAmount(), activeProduct.getMaxAmount());
        loanAmount = createNumberField("Loan amount",
                activeProduct.getMinAmount(),
                activeProduct.getMaxAmount(),
                AMOUNT_STEP,
                AMOUNT_DEFAULT,
                amountHelperText,
                activeProduct);

        final String termHelperText = String.format("The term has to be between %d and %d month", activeProduct.getMinTerm(), activeProduct.getMaxTerm());
        term = createNumberField("Term",
                activeProduct.getMinTerm(),
                activeProduct.getMaxTerm(),
                TERM_STEP,
                TERM_DEFAULT,
                termHelperText,
                activeProduct);

        monthlyInstalment.setReadOnly(true);
        monthlyInstalment.setPrefixComponent(new Div(new Text(activeProduct.getCurrency())));
        monthlyInstalment.setWidth(FIELD_WIDTH);

        next = createNextButton(activeProduct);

        updateMonthlyInstalment(activeProduct);
        add(loanAmount, term, monthlyInstalment, next);
    }
    
    private IntegerField createNumberField(String label, int min, int max, int step, int defaultValue, String helper, CashLoanProduct activeProduct) {
        IntegerField integerField = new IntegerField(label);
        
        integerField.setMin(min);
        integerField.setMax(max);
        integerField.setHasControls(true);
        integerField.setStep(step);
        integerField.setValue(defaultValue);
        integerField.setValueChangeMode(ValueChangeMode.LAZY);
        integerField.addValueChangeListener(e -> updateMonthlyInstalment(activeProduct));
        integerField.setWidth(FIELD_WIDTH);
        integerField.setHelperText(helper);

        return integerField;
    }

    private void updateMonthlyInstalment(CashLoanProduct activeProduct) {
        Integer presentValue = loanAmount.getValue();
        Integer requestedTerm = term.getValue();
        if (!isValid(activeProduct, presentValue, requestedTerm)) {
            monthlyInstalment.setValue(null);
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
            if (applicationId == null) return;

            Application application = applicationRepository.findById(applicationId).orElseThrow();
            updateMonthlyInstalment(activeProduct);
            application.setLoanAmount(loanAmount.getValue());
            application.setTerm(term.getValue());
            application.setMonthlyInstalment(monthlyInstalment.getValue().intValue());
            applicationRepository.save(application);

            this.getUI().flatMap(ui -> ui.navigate(IncomeView.class))
                    .ifPresent(view -> view.setApplicationId(applicationId));
        });

        return next;
    }
}
