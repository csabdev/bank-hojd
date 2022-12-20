package pillercs.app.vaadin.data.entity;

import lombok.*;
import pillercs.app.vaadin.services.underwriting.StepResult;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@Entity
@Table(name = "underwriting_step")
public class UnderwritingStepEntity {

    @GeneratedValue(generator = "underwriting_step_seq")
    @Id
    private Long underwritingStepId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "underwriting_id")
    private Underwriting underwriting;

    @NotBlank
    private String stepName;

    @Enumerated(EnumType.STRING)
    private StepResult result;

}
