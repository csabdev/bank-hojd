package pillercs.app.vaadin.data.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pillercs.app.vaadin.data.entity.Underwriting;
import pillercs.app.vaadin.data.entity.UnderwritingStepEntity;

import java.util.List;

public interface UnderwritingStepRepository extends JpaRepository<UnderwritingStepEntity, Long> {

    List<UnderwritingStepEntity> findAllByUnderwriting(Underwriting underwriting);

}