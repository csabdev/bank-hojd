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
public class Offer extends AbstractEntity {

    @GeneratedValue(generator = "offer_seq")
    @Id
    private Long offerId;

    @ManyToOne
    @JoinColumn(name = "underwriting_id")
    private Underwriting underwriting;

    @Positive
    @NotNull
    private Integer ordinal;

    @Positive
    @NotNull
    private Integer loanAmount;

    @Positive
    @NotNull
    private Integer term;

    @Positive
    @NotNull
    private Integer monthlyInstalment;

    @NotNull
    private Boolean accepted;

}
