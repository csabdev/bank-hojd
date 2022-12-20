package pillercs.app.vaadin.data.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import pillercs.app.vaadin.data.entity.Applicant;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ApplicantRepository extends JpaRepository<Applicant, Long> {

    Optional<Applicant> findByApplication_ApplicationId(Long applicationId);

    @Query("SELECT a FROM Applicant a JOIN FETCH a.client WHERE a.application.applicationId = :applicationId")
    List<Applicant> findApplicantsByApplicationIdWithClients(Long applicationId);

    @Query("SELECT count(apl) FROM Applicant apl WHERE apl.client.clientId = :clientId AND apl.created > :date")
    int countAllByClientAndCreatedBefore(Long clientId, LocalDateTime date);

}