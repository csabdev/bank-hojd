package pillercs.app.vaadin.data.enums;

import lombok.Getter;

@Getter
public enum IncomeFrequency {

    MONTHLY("monthly", 1d),
    YEARLY("yearly", 1d/12d),
    WEEKLY("weekly", 4d);

    private final String name;
    private final double multiplier;

    IncomeFrequency(String name, double multiplier) {
        this.name = name;
        this.multiplier = multiplier;
    }

}