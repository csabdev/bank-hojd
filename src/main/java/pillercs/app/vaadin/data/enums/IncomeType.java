package pillercs.app.vaadin.data.enums;

import lombok.Getter;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public enum IncomeType {

    SALARY("salary"),
    PENSION("pension"),
    RENT("rent"),
    OTHER("other");

    private final String name;

    IncomeType(String name) {
        this.name = name;
    }

    public static List<String> getNames() {
        return Arrays.stream(IncomeType.values())
                .map(IncomeType::getName)
                .collect(Collectors.toList());
    }
}