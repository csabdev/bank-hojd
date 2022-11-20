package pillercs.app.vaadin.data.enums;

import lombok.Getter;

public enum MaritalStatus {

    SINGLE("single"),
    MARRIED("married"),
    WIDOWED("widowed"),
    DIVORCED("divorced"),
    SEPARATED("separated"),
    REGISTERED_PARTNERSHIP("registered partnership");

    @Getter
    private final String name;

    MaritalStatus(String name) {
        this.name = name;
    }
}
