package pillercs.app.vaadin.data.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pillercs.app.vaadin.data.entity.Fraudster;

import java.util.List;

public interface FraudsterRepository extends JpaRepository<Fraudster, Long> {

    List<Fraudster> findByFirstNameAndLastName(String firstName, String lastName);

}