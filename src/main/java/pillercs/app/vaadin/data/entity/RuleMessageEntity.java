package pillercs.app.vaadin.data.entity;

import lombok.*;
import pillercs.app.vaadin.data.enums.MessageType;
import pillercs.app.vaadin.data.enums.RuleName;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@Entity
public class RuleMessageEntity extends AbstractEntity {

    @GeneratedValue(generator = "rule_message_seq")
    @Id
    private Long ruleMessageId;

    @Enumerated(value = EnumType.STRING)
    private RuleName ruleName;

    @Enumerated(value = EnumType.ORDINAL)
    private MessageType messageType;

    @NotBlank
    private String message;

}
