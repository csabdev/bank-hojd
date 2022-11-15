package pillercs.app.vaadin.data.entity;

import lombok.*;
import pillercs.app.vaadin.data.enums.MaritalStatus;
import pillercs.app.vaadin.data.enums.Role;
import pillercs.app.vaadin.data.enums.SchoolLevel;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "application_id")
    private Application application;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id")
    private Client client;

    @NotNull
    private Role role;

    @Enumerated
    private SchoolLevel highestLevelOfSchool;

    @Enumerated
    private MaritalStatus maritalStatus;

    @Positive
    private Integer householdNumber;

    @PositiveOrZero
    private Integer numberOfDependants;

    @PositiveOrZero
    private Integer outstandingLoansInstalment;

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
                ", role=" + role +
                ", highestLevelOfSchool=" + highestLevelOfSchool +
                ", maritalStatus=" + maritalStatus +
                ", householdNumber=" + householdNumber +
                ", numberOfDependants=" + numberOfDependants +
                ", outstandingLoansInstalment=" + outstandingLoansInstalment +
                '}';
    }
}
