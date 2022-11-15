package pillercs.app.vaadin.data.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pillercs.app.vaadin.data.entity.Address;

public interface AddressRepository extends JpaRepository<Address, Long> {
}