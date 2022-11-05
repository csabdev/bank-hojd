package pillercs.app.vaadin.data.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pillercs.app.vaadin.data.entity.Client;

public interface ClientRepository extends JpaRepository<Client, Long> {
}