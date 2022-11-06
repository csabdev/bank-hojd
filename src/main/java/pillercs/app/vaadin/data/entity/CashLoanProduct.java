package pillercs.app.vaadin.data.entity;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@Entity
public class CashLoanProduct extends AbstractEntity {

    @GeneratedValue(generator = "applicant_seq")
    @Id
    private Long cashLoanProductId;

    @NotNull
    private Integer minAmount;

    @NotNull
    private Integer maxAmount;

    @NotNull
    private Integer minTerm;

    @NotNull
    private Integer maxTerm;

    @NotNull
    private Double interestRate;

    @NotNull
    private String currency;

    @NotNull
    private LocalDateTime validFrom;

    private LocalDateTime validTo;

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;

        CashLoanProduct other = (CashLoanProduct) obj;

        return cashLoanProductId != null && cashLoanProductId.equals(other.getCashLoanProductId());
    }

    @Override
    public int hashCode() {
        return this.getClass().hashCode();
    }

    @Override
    public String toString() {
        return "CashLoanProduct{" +
                "cashLoanProductId=" + cashLoanProductId +
                ", minAmount=" + minAmount +
                ", maxAmount=" + maxAmount +
                ", minTerm=" + minTerm +
                ", maxTerm=" + maxTerm +
                ", interestRate=" + interestRate +
                ", currency='" + currency + '\'' +
                ", validFrom=" + validFrom +
                ", validTo=" + validTo +
                '}';
    }
}
