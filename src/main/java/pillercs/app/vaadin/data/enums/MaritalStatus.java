package pillercs.app.vaadin.data.enums;

public enum MaritalStatus {

    SINGLE("single"),
    MARRIED("married"),
    WIDOWED("widowed"),
    DIVORCED("divorced"),
    SEPARATED("separated"),
    REGISTERED_PARTNERSHIP("registered_partnership");

    private final String name;

    MaritalStatus(String name) {
        this.name = name;
    }
}
