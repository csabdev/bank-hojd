package pillercs.app.vaadin.data.enums;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;


public enum Gender {

    FEMALE("female"),
    MALE("male");

    @Getter
    private final String name;

    Gender(String name) {
        this.name = name;
    }

    public static List<String> getNames() {
        List<String> names = new ArrayList<>();

        for (Gender gender : Gender.values()) {
            names.add(gender.name);
        }

        return names;
    }
}
