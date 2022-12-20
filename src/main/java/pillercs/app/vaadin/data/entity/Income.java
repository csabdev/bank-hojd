package pillercs.app.vaadin.data.entity;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@Entity
public class Income extends AbstractEntity {

    @GeneratedValue(generator = "income_seq")
    @Id
    private Long incomeId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "applicant_id")
    private Applicant applicant;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employer_id")
    private Employer employer;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "income_type_id")
    private IncomeType type;

    @Positive
    @NotNull
    private Long amount;

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;

        Income other = (Income) obj;

        return incomeId != null && incomeId.equals(other.getIncomeId());
    }

    @Override
    public int hashCode() {
        return this.getClass().hashCode();
    }

    @Override
    public String toString() {
        return "Income{" +
                "id=" + getIncomeId() +
                ", type='" + type + '\'' +
                ", amount=" + amount +
                '}';
    }
}
