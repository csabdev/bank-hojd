package pillercs.app.vaadin.data.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pillercs.app.vaadin.data.entity.Employer;

import java.util.List;

public interface EmployerRepository extends JpaRepository<Employer, Long> {

    List<Employer> findAllByApplication_ApplicationId(long applicationId);

}