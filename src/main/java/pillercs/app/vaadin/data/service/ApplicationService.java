package pillercs.app.vaadin.data.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import pillercs.app.vaadin.data.entity.Application;
import pillercs.app.vaadin.data.enums.ApplicationState;
import pillercs.app.vaadin.data.repository.ApplicationRepository;
import pillercs.app.vaadin.security.SecurityService;

@Service
@RequiredArgsConstructor
public class ApplicationService {

    private final ApplicationRepository applicationRepository;
    private final SecurityService securityService;

    private static final String APP = "application";

    public Application getNewApplication() {
        UserDetails user = securityService.getAuthenticatedUser();

        return applicationRepository.save(Application.builder()
                .createdByUser(user == null ? APP : user.getUsername())
                .state(ApplicationState.CREATED)
                .build());
    }
}
