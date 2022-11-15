package pillercs.app.vaadin.data.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pillercs.app.vaadin.data.entity.Employer;

public interface EmployerRepository extends JpaRepository<Employer, Long> {
}