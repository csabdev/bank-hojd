package pillercs.app.vaadin.data.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pillercs.app.vaadin.data.entity.Underwriting;

public interface UnderwritingRepository extends JpaRepository<Underwriting, Long> {
}