package pillercs.app.vaadin.data.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pillercs.app.vaadin.data.entity.AuthoritiesEntity;

public interface AuthoritiesEntityRepository extends JpaRepository<AuthoritiesEntity, String> {
}