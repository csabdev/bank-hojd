package pillercs.app.vaadin.components;

import com.vaadin.flow.component.AbstractField;
import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;

public class FormattedIntegerLayout extends HorizontalLayout {

    private final FormattedIntegerField integerField;
    private final Icon minus;
    private final Icon plus;

    public FormattedIntegerLayout(String label, long step) {
        integerField = new FormattedIntegerField(label);
        integerField.setSizeFull();

        plus = new Icon(VaadinIcon.PLUS_CIRCLE_O);
        plus.addClickListener(e -> integerField.increase(step));
        minus = new Icon(VaadinIcon.MINUS_CIRCLE_O);
        minus.addClickListener(e -> integerField.decrease(step));

        setAlignItems(Alignment.BASELINE);

        add(minus, integerField, plus);
    }

    public void setLimits(int min, int max) {
        integerField.setLimits(min, max);
    }

    public void setPrefix(String prefix) {
        integerField.setPrefixComponent(new Div(new Text(prefix)));
    }

    public void setHelperText(String text) {
        integerField.setHelperText(text);
    }

    public void setValue(int value) {
        integerField.setValue(value);
    }

    public void addValueChangeListener(HasValue.ValueChangeListener<? super AbstractField.ComponentValueChangeEvent<TextField, String>> listener) {
        integerField.addValueChangeListener(listener);
    }

    public int getValue() {
        return integerField.getIntegerValue();
    }

    public void removeControls() {
        remove(minus, plus);
    }

    public void setReadOnly(boolean readOnly) {
        integerField.setReadOnly(readOnly);
    }

}
