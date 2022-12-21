package pillercs.app.vaadin.data.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pillercs.app.vaadin.data.entity.UserEntity;

public interface UserEntityRepository extends JpaRepository<UserEntity, String> {
}