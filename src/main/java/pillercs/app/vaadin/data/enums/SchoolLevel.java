package pillercs.app.vaadin.data.enums;

public enum SchoolLevel {

    NONE("none"),
    PRIMARY("primary"),
    SECONDARY("secondary"),
    DEGREE("degree");

    private final String name;

    SchoolLevel(String name) {
        this.name = name;
    }
}
