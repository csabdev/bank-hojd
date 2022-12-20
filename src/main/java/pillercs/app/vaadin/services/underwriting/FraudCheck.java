package pillercs.app.vaadin.services.underwriting;

import com.vaadin.flow.spring.annotation.SpringComponent;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import pillercs.app.vaadin.data.entity.Applicant;
import pillercs.app.vaadin.data.enums.RuleName;
import pillercs.app.vaadin.data.repository.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@SpringComponent
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class FraudCheck extends UnderwritingStep {

    private final FraudsterRepository fraudsterRepository;
    private final ApplicantRepository applicantRepository;

    private final static int MAX_APPLICATION_COUNT = 5;
    private final static String STEP_NAME = "Fraud check";

    public FraudCheck(UnderwritingStepDetailRepository underwritingStepDetailRepository,
                      FraudsterRepository fraudsterRepository,
                      ApplicantRepository applicantRepository,
                      UnderwritingRepository underwritingRepository,
                      UnderwritingStepRepository underwritingStepRepository,
                      RuleMessageRepository ruleMessageRepository) {
        super(underwritingStepDetailRepository, underwritingRepository, underwritingStepRepository, ruleMessageRepository);
        this.fraudsterRepository = fraudsterRepository;
        this.applicantRepository = applicantRepository;
    }

    @Override
    public void run(long applicationId, long underwritingId) {
        final List<Applicant> applicants = applicantRepository.findApplicantsByApplicationIdWithClients(applicationId);

        List<UnderwritingRuleDetailDto> details = new ArrayList<>();

        details.add(
                handleRuleResult(RuleName.FRAUDSTER_APPLICANTS, !hasFraudsterApplicant(applicants))
        );
        details.add(
                handleRuleResult(RuleName.APPLICATION_COUNT, !isApplicationCountLimitExceeded(applicants))
        );

        saveResults(STEP_NAME, underwritingId, details);
    }

    private boolean hasFraudsterApplicant(List<Applicant> applicants) {
        List<Applicant> fraudsters = new ArrayList<>();

        for (Applicant applicant : applicants) {
            if (!fraudsterRepository.findByFirstNameAndLastName(applicant.getClient().getFirstName(),
                    applicant.getClient().getLastName()).isEmpty()) {
                fraudsters.add(applicant);
            }
        }

        return !fraudsters.isEmpty();
    }

    private boolean isApplicationCountLimitExceeded(List<Applicant> applicants) {
        List<Applicant> fraudsters = new ArrayList<>();

        for (Applicant applicant : applicants) {
            if (applicantRepository.countAllByClientAndCreatedBefore(applicant.getClient().getClientId(),
                    LocalDateTime.now().minusMonths(6)) > MAX_APPLICATION_COUNT) {
                fraudsters.add(applicant);
            }
        }

        return !fraudsters.isEmpty();
    }

}
