package pillercs.app.vaadin.data.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import pillercs.app.vaadin.data.entity.Underwriting;

import java.util.List;
import java.util.Optional;

public interface UnderwritingRepository extends JpaRepository<Underwriting, Long> {

    @Query("SELECT u FROM Underwriting u JOIN FETCH u.steps WHERE u.application.applicationId = :applicationId")
    List<Underwriting> findByApplicationIdWithSteps(long applicationId);

    @Query("SELECT u FROM Underwriting u WHERE u.underwritingId = " +
            "(SELECT MAX(u.underwritingId) FROM Underwriting u WHERE u.application.applicationId = :applicationId)")
    Optional<Underwriting> findLatestByApplicationId(long applicationId);
}