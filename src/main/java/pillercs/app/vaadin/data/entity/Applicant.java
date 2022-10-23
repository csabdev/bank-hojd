package pillercs.app.vaadin.data.entity;

import lombok.*;
import pillercs.app.vaadin.data.enums.Role;

import javax.persistence.*;
import java.time.LocalDate;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@Entity
public class Applicant extends AbstractEntity {

    @GeneratedValue(generator = "applicant_seq")
    @Id
    private Long applicantId;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "application_id")
    private Application application;

    private String firstName;
    private String lastName;
    private LocalDate dateOfBirth;
    private String mothersName;
    private Role role;

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;

        Applicant other = (Applicant) obj;

        return applicantId != null && applicantId.equals(other.getApplicantId());
    }

    @Override
    public int hashCode() {
        return this.getClass().hashCode();
    }

    @Override
    public String toString() {
        return "Applicant{" +
                "applicantId=" + applicantId +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", dateOfBirth=" + dateOfBirth +
                ", mothersName='" + mothersName + '\'' +
                ", role=" + role +
                '}';
    }
}
