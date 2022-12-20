package pillercs.app.vaadin.services.underwriting;

import com.vaadin.flow.spring.annotation.SpringComponent;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import pillercs.app.vaadin.data.entity.*;
import pillercs.app.vaadin.data.enums.RuleName;
import pillercs.app.vaadin.data.repository.*;
import pillercs.app.vaadin.views.Utils;

import java.util.ArrayList;
import java.util.List;

@SpringComponent
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class Budget extends UnderwritingStep {

    private final UnderwritingRepository underwritingRepository;
    private final ApplicationRepository applicationRepository;
    private final CashLoanProductRepository cashLoanProductRepository;
    private final OfferRepository offerRepository;
    private final IncomeRepository incomeRepository;
    private final ApplicantRepository applicantRepository;

    private static final String STEP_NAME = "Budget";
    private static final Double MAX_DTI = 0.5;

    public Budget(UnderwritingStepDetailRepository underwritingStepDetailRepository,
                  UnderwritingRepository underwritingRepository,
                  UnderwritingStepRepository underwritingStepRepository,
                  ApplicationRepository applicationRepository,
                  CashLoanProductRepository cashLoanProductRepository,
                  OfferRepository offerRepository,
                  IncomeRepository incomeRepository,
                  ApplicantRepository applicantRepository,
                  RuleMessageRepository ruleMessageRepository) {
        super(underwritingStepDetailRepository, underwritingRepository, underwritingStepRepository, ruleMessageRepository);
        this.underwritingRepository = underwritingRepository;
        this.applicationRepository = applicationRepository;
        this.cashLoanProductRepository = cashLoanProductRepository;
        this.offerRepository = offerRepository;
        this.incomeRepository = incomeRepository;
        this.applicantRepository = applicantRepository;
    }

    @Override
    public void run(long applicationId, long underwritingId) {
        CashLoanProduct cashLoanProduct = cashLoanProductRepository.findCashLoanProductByValidToIsNull().orElseThrow();

        List<UnderwritingRuleDetailDto> details = new ArrayList<>();

        double dispensableIncome = calculateDispensableIncome(applicationId);

        details.add(
                handleRuleResult(RuleName.OFFER_AVAILABILITY, checkIfOffersAreAvailable(cashLoanProduct, dispensableIncome))
        );

        details.add(
                handleRuleResult(RuleName.OFFER_CALCULATION, calculateOffers(
                        applicationId,
                        underwritingId,
                        cashLoanProduct,
                        dispensableIncome
                ))
        );

        saveResults(STEP_NAME, underwritingId, details);
    }

    private boolean checkIfOffersAreAvailable(CashLoanProduct cashLoanProduct, double dispensableIncome) {
        int minMonthlyInstalment = Utils.calculateMonthlyInstalment(cashLoanProduct.getInterestRate(), cashLoanProduct.getMinAmount(), cashLoanProduct.getMaxTerm());

        return minMonthlyInstalment <= dispensableIncome;
    }

    private boolean calculateOffers(long applicationId, long underwritingId, CashLoanProduct cashLoanProduct, double dispensableIncome) {
        Underwriting underwriting = underwritingRepository.getReferenceById(underwritingId);
        Application application = applicationRepository.findById(applicationId).orElseThrow();

        double interestRate = cashLoanProduct.getInterestRate();

        List<Offer> offers = new ArrayList<>();
        int ordinal = 1;
        //Requested offer
        int monthlyInstalmentRequested = Utils.calculateMonthlyInstalment(interestRate, application.getLoanAmount(), application.getTerm());
        if (monthlyInstalmentRequested <= dispensableIncome) {
            offers.add(Offer.builder()
                    .underwriting(underwriting)
                    .ordinal(ordinal++)
                    .loanAmount(application.getLoanAmount())
                    .term(application.getTerm())
                    .monthlyInstalment(monthlyInstalmentRequested)
                    .accepted(false)
                    .build());
        }

        //Slight increase
        int amountInc = (int) Math.floor(application.getLoanAmount() * 1.2);
        int monthlyInstalmentInc = Utils.calculateMonthlyInstalment(interestRate, amountInc, application.getTerm());
        if (monthlyInstalmentInc <= dispensableIncome) {
            offers.add(Offer.builder()
                    .underwriting(underwriting)
                    .ordinal(ordinal++)
                    .loanAmount(amountInc)
                    .term(application.getTerm())
                    .monthlyInstalment(monthlyInstalmentInc)
                    .accepted(false)
                    .build());
        }

        //Max amount, max term
        int monthlyInstalmentMax = Utils.calculateMonthlyInstalment(interestRate, cashLoanProduct.getMaxAmount(), cashLoanProduct.getMaxTerm());
        if (monthlyInstalmentMax <= dispensableIncome) {
            offers.add(Offer.builder()
                    .underwriting(underwriting)
                    .ordinal(ordinal)
                    .loanAmount(cashLoanProduct.getMaxAmount())
                    .term(cashLoanProduct.getMaxTerm())
                    .monthlyInstalment(monthlyInstalmentMax)
                    .accepted(false)
                    .build());
        }

        if (!offers.isEmpty()) offerRepository.saveAll(offers);

        return !offers.isEmpty();
    }

    private double calculateDispensableIncome(long applicationId) {
        List<Income> incomes = incomeRepository.findAllByApplicationId(applicationId);
        Double incomeSum = Utils.sumIncomes(incomes);

        Applicant applicant = applicantRepository.findByApplication_ApplicationId(applicationId).orElseThrow();

        return (incomeSum * MAX_DTI) - applicant.getOutstandingLoansInstalment();
    }

}
