package pillercs.app.vaadin.data.entity;

import lombok.*;
import pillercs.app.vaadin.data.enums.ApplicationState;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
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

    @OneToMany(mappedBy = "application", cascade = CascadeType.ALL)
    private Set<Applicant> applicants;

    @NotNull
    @Enumerated(EnumType.STRING)
    private ApplicationState state;

    @NotEmpty
    private String createdByUser;

    @Positive
    private Integer loanAmount;

    @Positive
    private Integer term;

    @Positive
    private Integer monthlyInstalment;

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
                "applicationId=" + applicationId +
                ", createdByUser='" + createdByUser + '\'' +
                ", loanAmount=" + loanAmount +
                ", term=" + term +
                ", monthlyInstalment=" + monthlyInstalment +
                '}';
    }
}