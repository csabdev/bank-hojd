package pillercs.app.vaadin.data.entity;

import lombok.*;
import pillercs.app.vaadin.data.enums.IncomeType;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@Entity
public class Income extends AbstractEntity {

    @GeneratedValue
    @Id
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "application_id")
    private Application application;

    @NotNull
    @Enumerated(EnumType.STRING)
    private IncomeType type;

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

        return id != null && id.equals(other.getId());
    }

    @Override
    public int hashCode() {
        return this.getClass().hashCode();
    }

    @Override
    public String toString() {
        return "Income{" +
                "id=" + getId() +
                ", type='" + type + '\'' +
                ", amount=" + amount +
                '}';
    }
}
