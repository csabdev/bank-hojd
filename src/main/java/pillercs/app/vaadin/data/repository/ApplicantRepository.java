package pillercs.app.vaadin.data.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pillercs.app.vaadin.data.entity.Applicant;

public interface ApplicantRepository extends JpaRepository<Applicant, Long> {
}