package pillercs.app.vaadin.services.underwriting;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pillercs.app.vaadin.data.entity.*;
import pillercs.app.vaadin.data.enums.IncomeFrequency;
import pillercs.app.vaadin.data.enums.MessageType;
import pillercs.app.vaadin.data.repository.*;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BudgetTest {

    @InjectMocks
    Budget budget;

    @Mock
    CashLoanProductRepository cashLoanProductRepositoryMock;

    @Mock
    IncomeRepository incomeRepositoryMock;

    @Mock
    ApplicantRepository applicantRepositoryMock;

    @Mock
    RuleMessageRepository ruleMessageRepositoryMock;

    @Mock
    UnderwritingRepository underwritingRepositoryMock;

    @Mock
    ApplicationRepository applicationRepositoryMock;

    @Mock
    OfferRepository offerRepositoryMock;

    @Mock
    UnderwritingStepRepository underwritingStepRepositoryMock;

    @Mock
    UnderwritingStepDetailRepository underwritingStepDetailRepositoryMock;

    @Captor
    ArgumentCaptor<List<Offer>> offersCaptor;

    private static final long APPLICATION_ID = 1L;
    private static final long UNDERWRITING_ID = 1L;

    @BeforeEach
    void init() {
        CashLoanProduct cashLoanProduct = CashLoanProduct.builder()
                .interestRate(0.15)
                .minAmount(200_000)
                .maxAmount(10_000_000)
                .maxTerm(84)
                .build();
        when(cashLoanProductRepositoryMock.findCashLoanProductByValidToIsNull()).thenReturn(Optional.of(cashLoanProduct));

        List<Income> incomes = List.of(
                Income.builder()
                        .amount(10_000_000L)
                        .type(IncomeType.builder().frequency(IncomeFrequency.MONTHLY).build())
                        .build()
        );
        when(incomeRepositoryMock.findAllByApplicationId(anyLong())).thenReturn(incomes);

        Applicant applicant = Applicant.builder().outstandingLoansInstalment(15_000).build();
        when(applicantRepositoryMock.findByApplication_ApplicationId(any())).thenReturn(Optional.of(applicant));

        lenient().when(ruleMessageRepositoryMock.getRuleMessageByRuleNameAndMessageType(any(), eq(MessageType.APPROVED)))
                .thenReturn(Optional.of("message"));

        Underwriting underwriting = Underwriting.builder().underwritingId(1L).build();
        when(underwritingRepositoryMock.getReferenceById(any())).thenReturn(underwriting);

        Application application = Application.builder()
                .loanAmount(400_000)
                .term(48)
                .build();
        when(applicationRepositoryMock.findById(any())).thenReturn(Optional.of(application));
    }

    @Test
    void run_AllPasses() {
        budget.run(APPLICATION_ID, UNDERWRITING_ID);

        verify(ruleMessageRepositoryMock, times(2)).getRuleMessageByRuleNameAndMessageType(any(), eq(MessageType.APPROVED));

        verify(offerRepositoryMock).saveAll(offersCaptor.capture());
        List<Offer> actualOffers = offersCaptor.getValue();
        assertEquals(3, actualOffers.size());
    }

    @Test
    void run_NonPass() {
        when(ruleMessageRepositoryMock.getRuleMessageByRuleNameAndMessageType(any(), eq(MessageType.DECLINED)))
                .thenReturn(Optional.of("message"));
        when(incomeRepositoryMock.findAllByApplicationId(anyLong())).thenReturn(List.of(
                Income.builder()
                        .amount(10_000L)
                        .type(IncomeType.builder().frequency(IncomeFrequency.MONTHLY).build())
                        .build()
        ));

        budget.run(APPLICATION_ID, UNDERWRITING_ID);

        verify(ruleMessageRepositoryMock, times(2)).getRuleMessageByRuleNameAndMessageType(any(), eq(MessageType.DECLINED));
    }

}