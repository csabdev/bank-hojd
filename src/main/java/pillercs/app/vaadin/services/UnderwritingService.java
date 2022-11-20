package pillercs.app.vaadin.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;
import pillercs.app.vaadin.data.entity.Application;
import pillercs.app.vaadin.data.entity.CashLoanProduct;
import pillercs.app.vaadin.data.entity.Offer;
import pillercs.app.vaadin.data.entity.Underwriting;
import pillercs.app.vaadin.data.repository.ApplicationRepository;
import pillercs.app.vaadin.data.repository.CashLoanProductRepository;
import pillercs.app.vaadin.data.repository.OfferRepository;
import pillercs.app.vaadin.data.repository.UnderwritingRepository;
import pillercs.app.vaadin.views.Utils;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Service
@Slf4j
@RequiredArgsConstructor
public class UnderwritingService {

    private final UnderwritingRepository underwritingRepository;
    private final ApplicationRepository applicationRepository;
    private final OfferRepository offerRepository;
    private final CashLoanProductRepository cashLoanProductRepository;

    @Async
    public ListenableFuture<Void> runUnderwriting(Long applicationId) {
        log.info("Underwriting started for application: {}", applicationId);

        try {
            Thread.sleep(ThreadLocalRandom.current().nextInt(3_000, 8_000));

            Application application = applicationRepository.findById(applicationId).orElseThrow();
            CashLoanProduct cashLoanProduct = cashLoanProductRepository.findCashLoanProductByValidToIsNull().orElseThrow();

            Underwriting underwriting = new Underwriting();
            underwriting.setApplication(application);
            underwriting.setResult("OK");
            underwriting = underwritingRepository.save(underwriting);

            Offer offer1 = Offer.builder()
                    .underwriting(underwriting)
                    .ordinal(1)
                    .loanAmount(application.getLoanAmount())
                    .term(application.getTerm())
                    .monthlyInstalment(application.getMonthlyInstalment())
                    .accepted(false)
                    .build();

            Offer offer2 = Offer.builder()
                    .underwriting(underwriting)
                    .ordinal(2)
                    .loanAmount(10_000_000)
                    .term(84)
                    .monthlyInstalment(Utils.calculateMonthlyInstalment(
                            cashLoanProduct.getInterestRate(),
                            10_000_000,
                            84))
                    .accepted(false)
                    .build();

            Offer offer3 = Offer.builder()
                    .underwriting(underwriting)
                    .ordinal(3)
                    .loanAmount(1_000_000)
                    .term(60)
                    .monthlyInstalment(Utils.calculateMonthlyInstalment(
                            cashLoanProduct.getInterestRate(),
                            1_000_000,
                            60))
                    .accepted(false)
                    .build();

            offerRepository.saveAll(List.of(offer1, offer2, offer3));

            log.info("Underwriting for application: {} is finished", applicationId);
            return null;
        } catch (InterruptedException e) {
            log.error(e.getMessage());
            return null;
        }
    }
}
