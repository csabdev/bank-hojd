package pillercs.app.vaadin.data.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import pillercs.app.vaadin.data.entity.RuleMessageEntity;
import pillercs.app.vaadin.data.enums.MessageType;
import pillercs.app.vaadin.data.enums.RuleName;

import java.util.Optional;

public interface RuleMessageRepository extends JpaRepository<RuleMessageEntity, Long> {

    @Query("SELECT rme.message FROM RuleMessageEntity rme WHERE rme.ruleName = :ruleName AND rme.messageType = :messageType")
    Optional<String> getRuleMessageByRuleNameAndMessageType(RuleName ruleName, MessageType messageType);

}