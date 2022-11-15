package pillercs.app.vaadin.data.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pillercs.app.vaadin.data.entity.IncomeType;

public interface IncomeTypeRepository extends JpaRepository<IncomeType, Long> {
}