package pillercs.app.vaadin.services;

import com.vaadin.flow.component.button.Button;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pillercs.app.vaadin.data.entity.Application;
import pillercs.app.vaadin.data.enums.ApplicationState;
import pillercs.app.vaadin.data.repository.ApplicantRepository;
import pillercs.app.vaadin.data.repository.ApplicationRepository;
import pillercs.app.vaadin.data.repository.UnderwritingRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WorkflowServiceTest {

    @InjectMocks
    WorkflowService workflowService;

    @Mock
    ApplicationRepository applicationRepositoryMock;

    @Mock
    ApplicantRepository applicantRepositoryMock;

    @Mock
    UnderwritingRepository underwritingRepositoryMock;

    @Captor
    ArgumentCaptor<Application> applicationArgumentCaptor;

    @Test
    void nextStep() {
        Application application = Application.builder()
                .state(ApplicationState.CREATED)
                .build();
        when(applicationRepositoryMock.findById(any())).thenReturn(Optional.of(application));

        workflowService.nextStep(new Button(), 1L);

        verify(applicationRepositoryMock).save(applicationArgumentCaptor.capture());

        Application savedApplication = applicationArgumentCaptor.getValue();

        assertEquals(ApplicationState.RECORD_BASIC_INFORMATION, savedApplication.getState());
    }
}