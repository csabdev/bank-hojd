package pillercs.app.vaadin.data.entity;

import lombok.*;
import pillercs.app.vaadin.data.enums.EmploymentType;

import javax.persistence.*;
import javax.validation.constraints.Future;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import java.time.LocalDate;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@Entity
public class Employer extends AbstractEntity {

    @GeneratedValue(generator = "employer_seq")
    @Id
    private Long employerId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "application_id")
    private Application application;

    @NotBlank
    private String name;

    @NotBlank
    private String taxNumber;

    @NotNull
    @Enumerated(EnumType.STRING)
    private EmploymentType employmentType;

    @NotNull
    @Past
    private LocalDate employmentStartDate;

    @Future
    private LocalDate employmentEndDate;

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;

        Employer other = (Employer) obj;

        return employerId != null && employerId.equals(other.getEmployerId());
    }

    @Override
    public int hashCode() {
        return this.getClass().hashCode();
    }

    @Override
    public String toString() {
        return "Employer{" +
                "employerId=" + employerId +
                ", name='" + name + '\'' +
                ", taxNumber='" + taxNumber + '\'' +
                '}';
    }
}
