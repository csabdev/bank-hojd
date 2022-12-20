package pillercs.app.vaadin.services.underwriting;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import pillercs.app.vaadin.data.entity.UnderwritingStepDetailEntity;
import pillercs.app.vaadin.data.entity.UnderwritingStepEntity;
import pillercs.app.vaadin.data.enums.MessageType;
import pillercs.app.vaadin.data.enums.RuleName;
import pillercs.app.vaadin.data.repository.RuleMessageRepository;
import pillercs.app.vaadin.data.repository.UnderwritingRepository;
import pillercs.app.vaadin.data.repository.UnderwritingStepDetailRepository;
import pillercs.app.vaadin.data.repository.UnderwritingStepRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public abstract class UnderwritingStep {

    private final UnderwritingStepDetailRepository underwritingStepDetailRepository;
    private final UnderwritingRepository underwritingRepository;
    private final UnderwritingStepRepository underwritingStepRepository;
    private final RuleMessageRepository ruleMessageRepository;

    abstract void run(long applicationId, long underwritingId);

    protected UnderwritingRuleDetailDto handleRuleResult(@NonNull RuleName ruleName, boolean result) {
        final UnderwritingRuleDetailDto detail = new UnderwritingRuleDetailDto();
        String message;

        if (result) {
            message = ruleMessageRepository.getRuleMessageByRuleNameAndMessageType(ruleName, MessageType.APPROVED)
                    .orElseThrow();

            detail.setResult(StepResult.APPROVED);
        } else {
            message = ruleMessageRepository.getRuleMessageByRuleNameAndMessageType(ruleName, MessageType.DECLINED)
                    .orElseThrow();

            detail.setResult(StepResult.DECLINED);
        }
        detail.setMessage(message);

        return detail;
    }

    protected void saveResults(String stepName, long underwritingId, List<UnderwritingRuleDetailDto> details) {
        final StepResult result = details.stream().anyMatch(d -> d.getResult() == StepResult.DECLINED)
                        ? StepResult.DECLINED
                        : StepResult.APPROVED;

        final UnderwritingStepEntity savedStep = createAndSaveUnderwritingStepEntityWithResult(stepName, underwritingId, result);

        final List<String> messages = details.stream()
                .map(UnderwritingRuleDetailDto::getMessage)
                .collect(Collectors.toList());

        saveMessages(savedStep, messages);
    }

    private UnderwritingStepEntity createAndSaveUnderwritingStepEntityWithResult(String stepName,
                                                                                 Long underwritingId,
                                                                                 StepResult result) {
        final UnderwritingStepEntity step = new UnderwritingStepEntity();
        step.setStepName(stepName);
        step.setResult(result);
        step.setUnderwriting(underwritingRepository.getReferenceById(underwritingId));
        return underwritingStepRepository.save(step);
    }

    private void saveMessages(UnderwritingStepEntity underwritingStep, List<String> messages) {
        final List<UnderwritingStepDetailEntity> underwritingStepDetails = new ArrayList<>();

        for (String message : messages) {
            UnderwritingStepDetailEntity detail = UnderwritingStepDetailEntity.builder()
                    .underwritingStep(underwritingStep)
                    .detail(message)
                    .build();
            underwritingStepDetails.add(detail);
        }

        underwritingStepDetailRepository.saveAll(underwritingStepDetails);
    }

}
