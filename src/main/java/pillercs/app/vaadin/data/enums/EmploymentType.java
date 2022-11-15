package pillercs.app.vaadin.data.enums;

import lombok.Getter;

public enum EmploymentType {

    PERMANENT("permanent"),
    TEMPORARY("temporary");

    @Getter
    private final String name;

    EmploymentType(String name) {
        this.name = name;
    }

}
