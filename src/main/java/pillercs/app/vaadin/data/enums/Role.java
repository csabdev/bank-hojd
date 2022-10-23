package pillercs.app.vaadin.data.enums;

import lombok.Getter;

@Getter
public enum Role {

    DEBTOR("debtor"),
    CODEBTOR("codebtor");

    private final String name;

    Role(String name) {
        this.name = name;
    }
}