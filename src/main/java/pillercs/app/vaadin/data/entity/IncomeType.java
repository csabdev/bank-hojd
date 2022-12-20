package pillercs.app.vaadin.data.entity;

import lombok.*;
import pillercs.app.vaadin.data.enums.IncomeFrequency;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@Entity
public class IncomeType extends AbstractEntity {

    @GeneratedValue(generator = "income_type_seq")
    @Id
    private Long incomeTypeId;

    @NotBlank
    private String name;

    @NotNull
    private Boolean isEmployerNeeded;

    @NotNull
    @Enumerated(value = EnumType.STRING)
    private IncomeFrequency frequency;

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;

        IncomeType other = (IncomeType) obj;

        return incomeTypeId != null && incomeTypeId.equals(other.getIncomeTypeId());
    }

    @Override
    public int hashCode() {
        return this.getClass().hashCode();
    }

    @Override
    public String toString() {
        return "IncomeType{" +
                "incomeTypeId=" + incomeTypeId +
                ", name='" + name + '\'' +
                ", isEmployerNeeded=" + isEmployerNeeded +
                ", frequency=" + frequency +
                '}';
    }
}
