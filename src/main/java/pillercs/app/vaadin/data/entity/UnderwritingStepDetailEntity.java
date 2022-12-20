package pillercs.app.vaadin.data.entity;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@Entity
public class UnderwritingStepDetailEntity {

    @GeneratedValue(generator = "underwriting_step_detail_seq")
    @Id
    private Long underwritingStepDetailId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "underwriting_step_id")
    private UnderwritingStepEntity underwritingStep;

    @NotBlank
    private String detail;

}
