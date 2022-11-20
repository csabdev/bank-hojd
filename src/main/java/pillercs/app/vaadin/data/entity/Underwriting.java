package pillercs.app.vaadin.data.entity;

import lombok.*;

import javax.persistence.*;

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

    private String result;

}
