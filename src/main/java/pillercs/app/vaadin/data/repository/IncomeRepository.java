package pillercs.app.vaadin.data.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import pillercs.app.vaadin.data.entity.Income;

import java.util.List;

public interface IncomeRepository extends JpaRepository<Income, Long> {

    @Query("SELECT i FROM Income i JOIN FETCH i.type WHERE i.applicant.application.applicationId = :applicationId")
    List<Income> findAllByApplicationId(long applicationId);

}