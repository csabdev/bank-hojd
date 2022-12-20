package pillercs.app.vaadin.services.underwriting;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;
import pillercs.app.vaadin.data.entity.Underwriting;
import pillercs.app.vaadin.data.entity.UnderwritingStepEntity;
import pillercs.app.vaadin.data.repository.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
@Slf4j
@RequiredArgsConstructor
public class UnderwritingService {

    private final UnderwritingRepository underwritingRepository;
    private final UnderwritingStepRepository underwritingStepRepository;
    private final ApplicationRepository applicationRepository;

    private final FraudCheck fraudCheck;
    private final Budget budget;
    private final ProductRules productRules;

    @Async
    public ListenableFuture<Void> runUnderwriting(Long applicationId) {
        log.info("Underwriting started for application: {}", applicationId);

        List<UnderwritingStage> underwritingStages = setUpUnderwritingStages();

        Underwriting underwriting = createNewUnderwriting(applicationId);

        runUnderwritingSteps(applicationId, underwriting, underwritingStages);

        UnderwritingResult result = evaluateResults(underwriting);

        underwriting.setResult(result);
        underwritingRepository.save(underwriting);

        //Just to show loading screen
        try {
            Thread.sleep(2_000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        return null;
    }

    private List<UnderwritingStage> setUpUnderwritingStages() {
        List<UnderwritingStage> underwritingStages = new ArrayList<>();

        UnderwritingStage firstStage = new UnderwritingStage(List.of(fraudCheck, budget, productRules));
        underwritingStages.add(firstStage);

        return underwritingStages;
    }

    private Underwriting createNewUnderwriting(long applicationId) {
        return underwritingRepository.save(Underwriting.builder()
                .application(applicationRepository.getReferenceById(applicationId))
                .build());
    }

    private void runUnderwritingSteps(long applicationId, Underwriting underwriting, List<UnderwritingStage> underwritingStages) {
        for (UnderwritingStage underwritingStage : underwritingStages) {
            List<CompletableFuture<Void>> futureResults = new ArrayList<>();

            for (UnderwritingStep underwritingStep : underwritingStage.getUnderwritingSteps()) {
                CompletableFuture underwritingResult = CompletableFuture.runAsync(() ->
                        underwritingStep.run(applicationId, underwriting.getUnderwritingId()));
                futureResults.add(underwritingResult);
            }

            CompletableFuture<Void> combinedFuture
                    = CompletableFuture.allOf(futureResults.toArray(new CompletableFuture[0]));
            combinedFuture.join();
        }
    }

    private UnderwritingResult evaluateResults(Underwriting underwriting) {
        List<UnderwritingStepEntity> underwritingStepEntities = underwritingStepRepository.findAllByUnderwriting(underwriting);

        for (UnderwritingStepEntity underwritingStep : underwritingStepEntities) {
            if (StepResult.DECLINED == underwritingStep.getResult()) {
                return UnderwritingResult.DECLINED;
            }
        }

        return UnderwritingResult.APPROVED;
    }

}
