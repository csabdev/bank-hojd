package pillercs.app.data.service;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import pillercs.app.data.entity.SamplePerson;

public interface SamplePersonRepository extends JpaRepository<SamplePerson, UUID> {

}