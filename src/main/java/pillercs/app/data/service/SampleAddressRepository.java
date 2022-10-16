package pillercs.app.data.service;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import pillercs.app.data.entity.SampleAddress;

public interface SampleAddressRepository extends JpaRepository<SampleAddress, UUID> {

}