package pillercs.app.vaadin.data.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pillercs.app.vaadin.data.entity.Income;

public interface IncomeRepository extends JpaRepository<Income, Long> {
}