package pillercs.app.vaadin.views.process.recordincome.components;

import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.shared.Registration;
import com.vaadin.flow.spring.annotation.SpringComponent;
import lombok.Getter;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import pillercs.app.vaadin.data.entity.Employer;
import pillercs.app.vaadin.data.entity.Income;

@SpringComponent
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class IncomeGrid extends Grid<Income> {

    private static final String GRID_HEIGHT = "200px";

    public IncomeGrid() {
        super(Income.class, false);

        setSizeFull();
        addColumn(i -> i.getType().getName()).setHeader("Type");
        addColumn(i -> {
            Employer emp = i.getEmployer();
            if (emp == null) return null;
            return emp.getName();
        }).setHeader("Employer");
        addColumn("amount").setHeader("Amount");
        getColumns().forEach(col -> col.setAutoWidth(true));
        setHeight(GRID_HEIGHT);

        addColumn(new ComponentRenderer<>(Button::new, (button, income) -> {
            button.addThemeVariants(ButtonVariant.LUMO_ICON,
                    ButtonVariant.LUMO_ERROR,
                    ButtonVariant.LUMO_TERTIARY);
            button.addClickListener(e -> fireEvent(new DeleteEvent(this, income)));
            button.setIcon(new Icon(VaadinIcon.TRASH));
        }));

        getColumns().forEach(col -> col.setAutoWidth(true));

    }

    public <T extends ComponentEvent<?>> Registration addListener(Class<T> eventType,
                                                                  ComponentEventListener<T> listener) {
        return getEventBus().addListener(eventType, listener);
    }

    @Getter
    public static class DeleteEvent extends ComponentEvent<IncomeGrid> {

        private final Income income;

        public DeleteEvent(IncomeGrid source, Income income) {
            super(source, false);
            this.income = income;
        }
    }
}
