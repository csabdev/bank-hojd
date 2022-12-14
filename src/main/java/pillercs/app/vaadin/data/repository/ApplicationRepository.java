package pillercs.app.vaadin.data.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import pillercs.app.vaadin.data.entity.Application;

import java.util.List;
import java.util.Optional;

public interface ApplicationRepository extends JpaRepository<Application, Long>, JpaSpecificationExecutor<Application> {

    @Query("select app from Application app left join fetch app.applicants where app.applicationId = :applicationId")
    Optional<Application> findWithApplicants(Long applicationId);

    @Query("select app from Application app left join fetch app.applicants appl left join fetch appl.client")
    List<Application> findAllWithApplicants(Pageable pageable);

}