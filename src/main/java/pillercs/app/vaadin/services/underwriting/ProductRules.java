package pillercs.app.vaadin.services.underwriting;

import com.vaadin.flow.spring.annotation.SpringComponent;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import pillercs.app.vaadin.data.entity.Employer;
import pillercs.app.vaadin.data.entity.Income;
import pillercs.app.vaadin.data.enums.RuleName;
import pillercs.app.vaadin.data.repository.*;
import pillercs.app.vaadin.views.Utils;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@SpringComponent
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ProductRules extends UnderwritingStep {

    private final IncomeRepository incomeRepository;
    private final EmployerRepository employerRepository;

    private static final int MIN_EMPLOYMENT = 3;
    private static final double MIN_INCOME = 100_000d;
    private static final String STEP_NAME = "Product rules";

    public ProductRules(UnderwritingStepDetailRepository underwritingStepDetailRepository,
                        UnderwritingRepository underwritingRepository,
                        UnderwritingStepRepository underwritingStepRepository,
                        IncomeRepository incomeRepository,
                        EmployerRepository employerRepository,
                        RuleMessageRepository ruleMessageRepository) {
        super(underwritingStepDetailRepository, underwritingRepository, underwritingStepRepository, ruleMessageRepository);
        this.incomeRepository = incomeRepository;
        this.employerRepository = employerRepository;
    }

    @Override
    public void run(long applicationId, long underwritingId) {
        List<Employer> employers = employerRepository.findAllByApplication_ApplicationId(applicationId);

        List<UnderwritingRuleDetailDto> details = new ArrayList<>();

        if (!employers.isEmpty()) {
            details.add(
                    handleRuleResult(RuleName.MIN_EMPLOYMENT, isMinEmploymentOk(employers))
            );

            details.add(
                    handleRuleResult(RuleName.EMPLOYMENT_END_DATE, isEmploymentEndDateOk(employers))
            );
        }

        details.add(
                handleRuleResult(RuleName.MIN_INCOME, isMinIncomeOk(applicationId))
        );

        saveResults(STEP_NAME, underwritingId, details);
    }

    private boolean isMinEmploymentOk(List<Employer> employers) {
        return employers.stream()
                .allMatch(employer -> employer.getEmploymentStartDate().isBefore(LocalDate.now().minusMonths(MIN_EMPLOYMENT)));
    }

    private boolean isEmploymentEndDateOk(List<Employer> employers) {
        return employers.stream()
                .anyMatch(employer -> {
                    LocalDate endDate = employer.getEmploymentEndDate();
                    return endDate == null || endDate.isAfter(LocalDate.now().plusMonths(6));
                });
    }

    private boolean isMinIncomeOk(long applicationId) {
        List<Income> incomes = incomeRepository.findAllByApplicationId(applicationId);
        double incomeSum = Utils.sumIncomes(incomes);
        return incomeSum >= MIN_INCOME;
    }

}
