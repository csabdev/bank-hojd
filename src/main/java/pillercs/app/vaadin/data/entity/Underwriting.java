package pillercs.app.vaadin.data.entity;

import lombok.*;
import pillercs.app.vaadin.services.underwriting.UnderwritingResult;

import javax.persistence.*;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@Entity
public class Underwriting extends AbstractEntity {

    @GeneratedValue(generator = "underwriting_seq")
    @Id
    private Long underwritingId;

    @ManyToOne
    @JoinColumn(name = "application_id")
    private Application application;

    @Enumerated(EnumType.STRING)
    private UnderwritingResult result;

    @OneToMany(mappedBy = "underwriting")
    private List<UnderwritingStepEntity> steps;

}
