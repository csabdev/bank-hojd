package pillercs.app.vaadin.services.underwriting;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pillercs.app.vaadin.data.entity.Employer;
import pillercs.app.vaadin.data.entity.Income;
import pillercs.app.vaadin.data.entity.IncomeType;
import pillercs.app.vaadin.data.entity.Underwriting;
import pillercs.app.vaadin.data.enums.IncomeFrequency;
import pillercs.app.vaadin.data.enums.MessageType;
import pillercs.app.vaadin.data.repository.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class ProductRulesTest {

    @InjectMocks
    ProductRules productRules;

    @Mock
    EmployerRepository employerRepositoryMock;

    @Mock
    IncomeRepository incomeRepositoryMock;

    @Mock
    RuleMessageRepository ruleMessageRepositoryMock;

    @Mock
    UnderwritingRepository underwritingRepositoryMock;

    @Mock
    UnderwritingStepRepository underwritingStepRepository;

    @Mock
    UnderwritingStepDetailRepository underwritingStepDetailRepositoryMock;

    @BeforeEach
    void init() {
        lenient().when(ruleMessageRepositoryMock.getRuleMessageByRuleNameAndMessageType(any(), eq(MessageType.APPROVED)))
                .thenReturn(Optional.of("approved"));
        lenient().when(ruleMessageRepositoryMock.getRuleMessageByRuleNameAndMessageType(any(), eq(MessageType.DECLINED)))
                .thenReturn(Optional.of("declined"));

        Underwriting underwriting = Underwriting.builder().underwritingId(1L).build();
        when(underwritingRepositoryMock.getReferenceById(any())).thenReturn(underwriting);
    }

    @Test
    void run_AllPass() {
        List<Employer> employers = List.of(
                Employer.builder()
                        .employmentStartDate(LocalDate.now().minusMonths(7))
                        .employmentEndDate(LocalDate.now().plusMonths(7))
                        .build()
        );
        when(employerRepositoryMock.findAllByApplication_ApplicationId(anyLong())).thenReturn(employers);

        List<Income> incomes = List.of(
                Income.builder()
                        .amount(10_000_000L)
                        .type(IncomeType.builder().frequency(IncomeFrequency.MONTHLY).build())
                        .build()
        );
        when(incomeRepositoryMock.findAllByApplicationId(anyLong())).thenReturn(incomes);

        productRules.run(1L, 1L);

        verify(ruleMessageRepositoryMock, times(3)).getRuleMessageByRuleNameAndMessageType(any(), eq(MessageType.APPROVED));
    }

    @Test
    void run_AllFail() {
        List<Employer> employers = List.of(
                Employer.builder()
                        .employmentStartDate(LocalDate.now().minusMonths(2))
                        .employmentEndDate(LocalDate.now().plusMonths(5))
                        .build()
        );
        when(employerRepositoryMock.findAllByApplication_ApplicationId(anyLong())).thenReturn(employers);

        List<Income> incomes = List.of(
                Income.builder()
                        .amount(10_000L)
                        .type(IncomeType.builder().frequency(IncomeFrequency.MONTHLY).build())
                        .build()
        );
        when(incomeRepositoryMock.findAllByApplicationId(anyLong())).thenReturn(incomes);

        productRules.run(1L, 1L);

        verify(ruleMessageRepositoryMock, times(3)).getRuleMessageByRuleNameAndMessageType(any(), eq(MessageType.DECLINED));
    }

}