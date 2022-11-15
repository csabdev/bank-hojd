package pillercs.app.vaadin.views.process.recordincome.components;

import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.shared.Registration;
import com.vaadin.flow.spring.annotation.SpringComponent;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import pillercs.app.vaadin.data.entity.Employer;
import pillercs.app.vaadin.data.entity.Income;
import pillercs.app.vaadin.data.repository.EmployerRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@SpringComponent
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class EmployerLayout extends VerticalLayout {

    EmployerRepository employerRepository;
    private final EmployerForm employerForm;
    private final EmployerGrid employerGrid;

    private final H1 instructions;
    private final Button addNewEmployer;

    @Getter
    private List<Employer> employerList = new ArrayList<>();

    @Setter
    private List<Income> incomeList = new ArrayList<>();

    public EmployerLayout(EmployerForm employerForm,
                          EmployerGrid employerGrid,
                          EmployerRepository employerRepository) {
        this.employerForm = employerForm;
        this.employerGrid = employerGrid;
        this.employerRepository = employerRepository;

        instructions = new H1("First, please record the client's employers, if he or she has any");

        employerForm.setVisible(false);

        addNewEmployer = createAddNewEmployerButton();

        configureListeners();

        employerGrid.setVisible(!employerList.isEmpty());

        add(instructions, addNewEmployer, employerForm, employerGrid);
    }

    private Button createAddNewEmployerButton() {
        Button addNewEmployer = new Button("Add new employer");
        addNewEmployer.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        addNewEmployer.addClickListener(e -> {
            employerForm.setEmployer(new Employer());
            employerForm.setVisible(true);
        });

        return addNewEmployer;
    }

    private void configureListeners() {
        employerForm.addListener(EmployerForm.AddEmployerEvent.class, e -> {
            if (e.getNewEmployer() == null) return;

            employerList.add(employerRepository.save(e.getNewEmployer()));
            fireEvent(new EmployerListChangedEvent(this, employerList));
            employerForm.clear();
            refreshGrid();
        });

        employerGrid.addListener(EmployerGrid.DeleteEvent.class, e -> {
            if (e.getEmployer() == null) return;

            Set<Employer> currentlyUsedEmployers = incomeList.stream().map(Income::getEmployer).collect(Collectors.toSet());
            if (currentlyUsedEmployers.contains(e.getEmployer())) {
                Notification.show("The employer is connected to an income, cannot delete").setDuration(10_000);
                return;
            }

            employerRepository.delete(e.getEmployer());

            employerList.remove(e.getEmployer());
            fireEvent(new EmployerListChangedEvent(this, employerList));
            refreshGrid();
        });
    }

    public void refreshGrid() {
        if (employerList.isEmpty()) {
            employerGrid.setItems(List.of());
            employerGrid.setVisible(false);
        } else {
            employerGrid.setItems(employerList);
            employerGrid.setVisible(true);
        }
    }

    public void setVisible(boolean visible) {
        this.employerForm.setVisible(visible);
        this.instructions.setVisible(visible);
        this.addNewEmployer.setVisible(visible);
        if (!employerList.isEmpty()) this.employerGrid.setVisible(visible);
    }

    public <T extends ComponentEvent<?>> Registration addListener(Class<T> eventType,
                                                                  ComponentEventListener<T> listener) {
        return getEventBus().addListener(eventType, listener);
    }

    @Getter
    public static class EmployerListChangedEvent extends ComponentEvent<EmployerLayout> {

        private final List<Employer> employerList;

        public EmployerListChangedEvent(EmployerLayout source, List<Employer> employerList) {
            super(source, false);
            this.employerList = employerList;
        }
    }

}
