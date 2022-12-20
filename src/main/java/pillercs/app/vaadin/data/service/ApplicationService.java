package pillercs.app.vaadin.data.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pillercs.app.vaadin.data.entity.Application;
import pillercs.app.vaadin.data.enums.ApplicationState;
import pillercs.app.vaadin.data.repository.ApplicationRepository;

@Service
@RequiredArgsConstructor
public class ApplicationService {

    private final ApplicationRepository applicationRepository;

    public Application getNewApplication() {
        return applicationRepository.save(Application.builder()
                .createdByUser("admin")
                .state(ApplicationState.CREATED)
                .build());
    }
}
