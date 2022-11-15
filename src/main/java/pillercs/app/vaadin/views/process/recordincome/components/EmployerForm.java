package pillercs.app.vaadin.views.process.recordincome.components;

import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.shared.Registration;
import com.vaadin.flow.spring.annotation.SpringComponent;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import pillercs.app.vaadin.data.entity.Employer;
import pillercs.app.vaadin.data.enums.EmploymentType;

@Setter
@SpringComponent
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class EmployerForm extends FormLayout {

    private final Binder<Employer> binder = new BeanValidationBinder<>(Employer.class);
    private final TextField name = new TextField("Company name");
    private final TextField taxNumber = new TextField("Tax number");
    private final ComboBox<EmploymentType> employmentType = new ComboBox<>("Employment type");
    private final DatePicker employmentStartDate = new DatePicker("Employment start date");
    private final DatePicker employmentEndDate = new DatePicker("Employment end date");
    private Employer employer;

    public EmployerForm() {
        addClassName("new-employer-form");
        name.setValueChangeMode(ValueChangeMode.EAGER);
        taxNumber.setValueChangeMode(ValueChangeMode.EAGER);

        employmentType.setItems(EmploymentType.values());
        employmentType.setItemLabelGenerator(EmploymentType::getName);

        employmentType.addValueChangeListener(e -> {
           if (e.getValue() == EmploymentType.PERMANENT) {
               employmentEndDate.clear();
               employmentEndDate.setEnabled(false);
               return;
           }
           employmentEndDate.setEnabled(true);
        });

        HorizontalLayout hr = new HorizontalLayout(employmentType);
        setColspan(hr, 2);

        binder.bindInstanceFields(this);

        add(name, taxNumber, hr, employmentStartDate, employmentEndDate, createButtons());
    }

    private HorizontalLayout createButtons() {
        final Button addEmployer = new Button("Add employer");
        addEmployer.setEnabled(false);
        addEmployer.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        addEmployer.addClickListener(c -> {
            if (binder.writeBeanIfValid(employer)) {
                fireEvent(new AddEmployerEvent(this, employer));
            }
        });

        binder.addStatusChangeListener(e -> addEmployer.setEnabled(binder.isValid()));

        final Button cancel = new Button("Cancel");
        cancel.addClickListener(e -> clear());

        return new HorizontalLayout(addEmployer, cancel);
    }

    public void clear() {
        setVisible(false);
        employer = null;
        binder.readBean(null);
    }

    public <T extends ComponentEvent<?>> Registration addListener(Class<T> eventType,
                                                                  ComponentEventListener<T> listener) {
        return getEventBus().addListener(eventType, listener);
    }

    @Getter
    public static class AddEmployerEvent extends ComponentEvent<EmployerForm> {

        private final Employer newEmployer;

        public AddEmployerEvent(EmployerForm source, Employer employer) {
            super(source, false);
            this.newEmployer = employer;
        }
    }

}
