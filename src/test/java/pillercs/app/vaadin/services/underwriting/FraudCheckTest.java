package pillercs.app.vaadin.services.underwriting;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pillercs.app.vaadin.data.entity.Applicant;
import pillercs.app.vaadin.data.entity.Client;
import pillercs.app.vaadin.data.entity.Fraudster;
import pillercs.app.vaadin.data.entity.Underwriting;
import pillercs.app.vaadin.data.enums.MessageType;
import pillercs.app.vaadin.data.repository.*;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FraudCheckTest {

    @InjectMocks
    private FraudCheck fraudCheck;

    @Mock
    ApplicantRepository applicantRepositoryMock;

    @Mock
    FraudsterRepository fraudsterRepositoryMock;

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

        Applicant applicant = Applicant.builder()
                .client(Client.builder().firstName("firtsName").lastName("lastName").build())
                .build();
        when(applicantRepositoryMock.findApplicantsByApplicationIdWithClients(any())).thenReturn(List.of(applicant));
    }

    @Test
    void run_AllPass() {
        when(fraudsterRepositoryMock.findByFirstNameAndLastName(any(), any())).thenReturn(List.of());
        when(applicantRepositoryMock.countAllByClientAndCreatedBefore(any(), any())).thenReturn(0);

        fraudCheck.run(1L, 1L);

        verify(ruleMessageRepositoryMock, times(2)).getRuleMessageByRuleNameAndMessageType(any(), eq(MessageType.APPROVED));
    }

    @Test
    void run_AllFail() {
        when(fraudsterRepositoryMock.findByFirstNameAndLastName(any(), any())).thenReturn(List.of(new Fraudster()));
        when(applicantRepositoryMock.countAllByClientAndCreatedBefore(any(), any())).thenReturn(10);

        fraudCheck.run(1L, 1L);

        verify(ruleMessageRepositoryMock, times(2)).getRuleMessageByRuleNameAndMessageType(any(), eq(MessageType.DECLINED));
    }

}