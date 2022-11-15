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

@SpringComponent
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class EmployerGrid extends Grid<Employer> {

    private static final String GRID_HEIGHT = "200px";

    public EmployerGrid() {
        super(Employer.class, false);

        addColumn("name").setHeader("Employer name");
        addColumn("taxNumber").setHeader("Tax number");
        addColumn(e -> e.getEmploymentType().getName()).setHeader("Employment type");
        addColumn("employmentStartDate").setHeader("Employment start date");
        addColumn("employmentEndDate").setHeader("Employment end date");
        addColumn(new ComponentRenderer<>(Button::new, (button, employer) -> {
            button.addThemeVariants(ButtonVariant.LUMO_ICON,
                    ButtonVariant.LUMO_ERROR,
                    ButtonVariant.LUMO_TERTIARY);
            button.addClickListener(e -> fireEvent(new DeleteEvent(this, employer)));
            button.setIcon(new Icon(VaadinIcon.TRASH));
        }));

        getColumns().forEach(col -> col.setAutoWidth(true));
        setHeight(GRID_HEIGHT);
    }

    public <T extends ComponentEvent<?>> Registration addListener(Class<T> eventType,
                                                                  ComponentEventListener<T> listener) {
        return getEventBus().addListener(eventType, listener);
    }

    @Getter
    public static class DeleteEvent extends ComponentEvent<EmployerGrid> {

        private final Employer employer;

        public DeleteEvent(EmployerGrid source, Employer employer) {
            super(source, false);
            this.employer = employer;
        }
    }
}
