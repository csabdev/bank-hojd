package pillercs.app.vaadin.services.underwriting;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pillercs.app.vaadin.data.entity.Application;
import pillercs.app.vaadin.data.entity.Underwriting;
import pillercs.app.vaadin.data.entity.UnderwritingStepEntity;
import pillercs.app.vaadin.data.repository.ApplicationRepository;
import pillercs.app.vaadin.data.repository.UnderwritingRepository;
import pillercs.app.vaadin.data.repository.UnderwritingStepRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UnderwritingServiceTest {

    @InjectMocks
    UnderwritingService underwritingService;

    @Mock
    FraudCheck fraudCheckMock;

    @Mock
    ProductRules productRulesMock;

    @Mock
    Budget budgetMock;

    @Mock
    UnderwritingRepository underwritingRepositoryMock;

    @Mock
    ApplicationRepository applicationRepositoryMock;

    @Mock
    UnderwritingStepRepository underwritingStepRepositoryMock;

    @Captor
    ArgumentCaptor<Underwriting> underwritingArgumentCaptor;

    @Test
    void runUnderwriting() {
        when(underwritingRepositoryMock.save(any())).thenReturn(Underwriting.builder().underwritingId(1L).build());
        when(applicationRepositoryMock.getReferenceById(any())).thenReturn(new Application());
        when(underwritingStepRepositoryMock.findAllByUnderwriting(any())).thenReturn(List.of(
                UnderwritingStepEntity.builder().result(StepResult.APPROVED).build()
        ));

        underwritingService.runUnderwriting(1L);

        verify(underwritingRepositoryMock, times(2)).save(underwritingArgumentCaptor.capture());
        List<Underwriting> savedUnderwritings = underwritingArgumentCaptor.getAllValues();
        assertEquals(UnderwritingResult.APPROVED, savedUnderwritings.get(1).getResult());
    }
}