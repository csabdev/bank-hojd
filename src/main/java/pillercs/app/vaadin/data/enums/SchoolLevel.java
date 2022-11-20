package pillercs.app.vaadin.data.enums;

import lombok.Getter;

public enum SchoolLevel {

    NONE("none"),
    PRIMARY("primary"),
    SECONDARY("secondary"),
    DEGREE("degree");

    @Getter
    private final String name;

    SchoolLevel(String name) {
        this.name = name;
    }
}
