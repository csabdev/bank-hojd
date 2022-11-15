package pillercs.app.vaadin.views.process.recordincome.components;

import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.shared.Registration;
import com.vaadin.flow.spring.annotation.SpringComponent;
import lombok.Getter;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import pillercs.app.vaadin.data.entity.Employer;
import pillercs.app.vaadin.data.entity.Income;

import java.util.ArrayList;
import java.util.List;

@SpringComponent
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class IncomeLayout extends VerticalLayout {

    private final IncomeForm incomeForm;
    private final IncomeGrid incomeGrid;
    private final H1 h1 = new H1("Add incomes to the client");
    private final Button addNewIncome;

    private List<Income> incomeList = new ArrayList<>();
    private List<Employer> employerList = new ArrayList<>();

    public IncomeLayout(IncomeForm incomeForm, IncomeGrid incomeGrid) {
        this.incomeForm = incomeForm;
        this.incomeGrid = incomeGrid;

        incomeForm.setVisible(false);

        addNewIncome = createAddNewIncomeButton();
        addNewIncome.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        configureListeners();

        incomeGrid.setVisible(!incomeList.isEmpty());

        add(h1, addNewIncome, incomeForm, incomeGrid);
    }

    private Button createAddNewIncomeButton() {
        Button addNewIncome = new Button("Add new income");

        addNewIncome.addClickListener(e -> {
            incomeForm.setIncome(new Income());
            incomeForm.setVisible(true);
        });

        return addNewIncome;
    }

    private void configureListeners() {
        incomeForm.addListener(IncomeForm.AddIncomeEvent.class, e -> {
            incomeList.add(e.getNewIncome());
            fireEvent(new IncomeListChangedEvent(this, incomeList));
            incomeForm.clear();
            refreshGrid();
        });

        incomeGrid.addListener(IncomeGrid.DeleteEvent.class, e -> {
            if (e.getIncome() == null) return;
            incomeList.remove(e.getIncome());
            fireEvent(new IncomeListChangedEvent(this, incomeList));
            refreshGrid();
        });
    }

    public void refreshGrid() {
        if (incomeList.isEmpty()) {
            incomeGrid.setItems(List.of());
            incomeGrid.setVisible(false);
        } else {
            incomeGrid.setItems(incomeList);
            incomeGrid.setVisible(true);
        }
    }

    public void setEmployerList(List<Employer> employerList) {
        this.employerList = employerList;
        incomeForm.refreshEmployers(employerList);
    }

    public void setVisible(boolean visible) {
        this.incomeForm.setVisible(visible);
        this.h1.setVisible(visible);
        this.addNewIncome.setVisible(visible);
        if (!incomeList.isEmpty()) this.incomeGrid.setVisible(visible);
    }

    public <T extends ComponentEvent<?>> Registration addListener(Class<T> eventType,
                                                                  ComponentEventListener<T> listener) {
        return getEventBus().addListener(eventType, listener);
    }

    @Getter
    public static class IncomeListChangedEvent extends ComponentEvent<IncomeLayout> {

        private final List<Income> incomeList;

        public IncomeListChangedEvent(IncomeLayout source, List<Income> incomeList) {
            super(source, false);
            this.incomeList = incomeList;
        }
    }
}
