package pillercs.app.vaadin.components;

import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

public class FormattedIntegerField extends TextField {

    private final DecimalFormat df;

    public FormattedIntegerField(String label) {
        super(label);
        df = new DecimalFormat();
        setUp();
    }

    private void setUp() {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
        symbols.setGroupingSeparator(' ');
        df.setGroupingSize(3);
        df.setDecimalFormatSymbols(symbols);

        setValueChangeMode(ValueChangeMode.LAZY);
        setAllowedCharPattern("[0-9]");
        addValueChangeListener(e -> setValue(df.format(convertToNumberValue(e.getValue()))));
    }

    public void setLimits(int min, int max) {
        addValueChangeListener(e -> {
            int newValue = convertToNumberValue(e.getValue());
            if (newValue < min) setInvalid(true);
            if (newValue > max) setValue(df.format(max));
        });
    }

    private int convertToNumberValue(String value) {
        return value.isBlank() ? 0 : Integer.valueOf(value.replace(" ", ""));
    }

    public void increase(long value) {
        setValue(df.format(convertToNumberValue(getValue()) + value));
    }

    public void decrease(long value) {
        setValue(df.format(convertToNumberValue(getValue()) - value));
    }

    public void setValue(int value) {
        setValue(df.format(value));
    }

    public int getIntegerValue() {
        return getValue().isBlank() ? 0 : Integer.valueOf(getValue().replace(" ", ""));
    }
}
