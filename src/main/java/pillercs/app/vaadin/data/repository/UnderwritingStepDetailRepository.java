package pillercs.app.vaadin.data.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pillercs.app.vaadin.data.entity.UnderwritingStepDetailEntity;

import java.util.List;

public interface UnderwritingStepDetailRepository extends JpaRepository<UnderwritingStepDetailEntity, Long> {

    List<UnderwritingStepDetailEntity> findAllByUnderwritingStep_UnderwritingStepId(Long underWritingStepId);

}