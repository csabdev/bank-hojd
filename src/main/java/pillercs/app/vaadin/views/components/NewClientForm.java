package pillercs.app.vaadin.views.components;

import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.shared.Registration;
import com.vaadin.flow.spring.annotation.SpringComponent;
import lombok.Getter;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import pillercs.app.vaadin.data.entity.Client;

@SpringComponent
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class NewClientForm extends FormLayout {

    private final Binder<Client> binder = new BeanValidationBinder<>(Client.class);
    private final TextField firstName = new TextField("First name");
    private final TextField lastName = new TextField("Last name");
    private final DatePicker dateOfBirth = new DatePicker("Birth date");
    private final TextField mothersName = new TextField("Mother's name");
    private Client client = new Client();

    public NewClientForm() {
        addClassName("new-client-form");

        binder.bindInstanceFields(this);

        add(firstName, lastName, dateOfBirth, mothersName, createAddButton());
    }

    private HorizontalLayout createAddButton() {
        final Button addClient = new Button("Add client");
        addClient.setEnabled(false);
        addClient.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        addClient.addClickListener(c -> {
            if (binder.writeBeanIfValid(client)) {
                fireEvent(new AddClientEvent(this, client));
            }
        });

        binder.addStatusChangeListener(e -> addClient.setEnabled(binder.isValid()));

        return new HorizontalLayout(addClient);
    }

    public <T extends ComponentEvent<?>> Registration addListener(Class<T> eventType,
                                                                  ComponentEventListener<T> listener) {
        return getEventBus().addListener(eventType, listener);
    }

    @Getter
    public static class AddClientEvent extends ComponentEvent<NewClientForm> {

        private final Client client;

        public AddClientEvent(NewClientForm source, Client client) {
            super(source, false);
            this.client = client;
        }
    }

}
