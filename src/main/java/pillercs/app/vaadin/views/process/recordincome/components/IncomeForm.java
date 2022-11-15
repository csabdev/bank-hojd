package pillercs.app.vaadin.views.process.recordincome.components;

import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.Result;
import com.vaadin.flow.data.binder.ValueContext;
import com.vaadin.flow.data.converter.Converter;
import com.vaadin.flow.shared.Registration;
import com.vaadin.flow.spring.annotation.SpringComponent;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import pillercs.app.vaadin.data.entity.Employer;
import pillercs.app.vaadin.data.entity.Income;
import pillercs.app.vaadin.data.entity.IncomeType;
import pillercs.app.vaadin.data.repository.IncomeTypeRepository;

import java.util.List;

@Setter
@SpringComponent
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class IncomeForm extends FormLayout {

    private final IncomeTypeRepository incomeTypeRepository;

    private final Binder<Income> binder = new BeanValidationBinder<>(Income.class);

    private final ComboBox<IncomeType> type = new ComboBox<>("Type of income");
    private final ComboBox<Employer> employer = new ComboBox<>("Employer");
    private final IntegerField amount = new IntegerField("Amount");

    private Income income = new Income();

    public IncomeForm(IncomeTypeRepository incomeTypeRepository) {
        this.incomeTypeRepository = incomeTypeRepository;

        addClassName("income-form");

        binder.forField(amount)
                .withConverter(new IntegerLongConverter())
                .bind("amount");
        binder.bindInstanceFields(this);

        type.setItems(this.incomeTypeRepository.findAll());
        type.setItemLabelGenerator(IncomeType::getName);
        type.addValueChangeListener(e -> {
            if (e.getValue() == null) return;

            if (e.getValue().getIsEmployerNeeded()) {
                employer.setEnabled(true);
            } else {
                employer.setEnabled(false);
                employer.setValue(null);
            }
        });

        employer.setItemLabelGenerator(Employer::getName);
        employer.setRequired(false);

        amount.setPrefixComponent(new Div(new Text("HUF")));
        amount.setHasControls(true);
        amount.setMin(1000);
        amount.setMax(10_000_000);
        amount.setStep(1000);

        add(type, employer, amount, createButtons());
    }

    private HorizontalLayout createButtons() {
        final Button addIncome = new Button("Add income");
        addIncome.setEnabled(false);
        addIncome.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        addIncome.addClickListener(c -> {
            if (binder.writeBeanIfValid(income)) {
                fireEvent(new AddIncomeEvent(this, income));
            }
        });

        binder.addStatusChangeListener(e -> {
            if (binder.isValid()) {
                IncomeType type = this.type.getValue();
                if (type != null && (type.getIsEmployerNeeded() == (employer.getValue() != null))) {
                    addIncome.setEnabled(true);
                    return;
                }
            }
            addIncome.setEnabled(false);
        });

        final Button cancel = new Button("Cancel");
        cancel.addClickListener(e -> clear());

        return new HorizontalLayout(addIncome, cancel);
    }

    public void clear() {
        setVisible(false);
        income = null;
        binder.readBean(null);
    }

    public void refreshEmployers(List<Employer> employerList) {
        employer.setItems(employerList);
    }

    public <T extends ComponentEvent<?>> Registration addListener(Class<T> eventType,
                                                                  ComponentEventListener<T> listener) {
        return getEventBus().addListener(eventType, listener);
    }

    @Getter
    public static class AddIncomeEvent extends ComponentEvent<IncomeForm> {

        private final Income newIncome;

        public AddIncomeEvent(IncomeForm source, Income income) {
            super(source, false);
            this.newIncome = income;
        }
    }

    public static class IntegerLongConverter implements Converter<Integer, Long> {
        @Override
        public Result<Long> convertToModel(Integer source, ValueContext valueContext) {
            return source == null ? Result.ok(0L) : Result.ok(source.longValue());
        }

        @Override
        public Integer convertToPresentation(Long target, ValueContext valueContext) {
            return target.intValue();
        }
    }


}
