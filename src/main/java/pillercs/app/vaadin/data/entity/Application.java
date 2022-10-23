package pillercs.app.vaadin.data.entity;

import lombok.*;

import javax.persistence.*;
import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@Entity
public class Application extends AbstractEntity {

    @GeneratedValue(generator = "application_seq")
    @Id
    private Long applicationId;

    @OneToMany(mappedBy = "application")
    private Set<Applicant> applicants;

    //@NotEmpty
    private String createdByUser;

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;

        Application other = (Application) obj;

        return applicationId != null && applicationId.equals(other.getApplicationId());
    }

    @Override
    public int hashCode() {
        return this.getClass().hashCode();
    }

    @Override
    public String toString() {
        return "Application{" +
                "id=" + getApplicationId() +
                ", recordedByUser='" + createdByUser + '\'' +
                '}';
    }
}