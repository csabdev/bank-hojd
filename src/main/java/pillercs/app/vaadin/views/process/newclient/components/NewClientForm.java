package pillercs.app.vaadin.views.process.newclient.components;

import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.shared.Registration;
import com.vaadin.flow.spring.annotation.SpringComponent;
import lombok.Getter;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import pillercs.app.vaadin.data.entity.Address;
import pillercs.app.vaadin.data.entity.Client;
import pillercs.app.vaadin.data.enums.Gender;
import pillercs.app.vaadin.data.repository.AddressRepository;
import pillercs.app.vaadin.data.repository.ClientRepository;

import java.util.Optional;

@SpringComponent
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class NewClientForm extends FormLayout {

    private final Binder<Client> binder = new BeanValidationBinder<>(Client.class);

    private final TextField firstName = new TextField("First name");
    private final TextField lastName = new TextField("Last name");
    private final DatePicker dateOfBirth = new DatePicker("Birth date");
    private final TextField mothersName = new TextField("Mother's name");
    private final ComboBox<Gender> gender = new ComboBox<>("Gender");
    private final TextField phoneNumber = new TextField("Phone number");
    private final EmailField emailAddress = new EmailField("E-mail");

    private final AddressForm addressForm;
    private final ClientRepository clientRepository;
    private final AddressRepository addressRepository;

    private Client client = new Client();

    public NewClientForm(AddressForm addressForm,
                         ClientRepository clientRepository,
                         AddressRepository addressRepository) {
        this.addressForm = addressForm;
        this.clientRepository = clientRepository;
        this.addressRepository = addressRepository;

        addClassName("new-client-form");

        binder.bindInstanceFields(this);

        gender.setItems(Gender.values());
        gender.setItemLabelGenerator(Gender::getName);

        setColspan(addressForm, 2);

        createSectionHeader("Personal data");
        add(firstName, lastName, dateOfBirth, mothersName, gender);

        createSectionHeader("Address");
        add(this.addressForm);

        createSectionHeader("Contact");
        add(phoneNumber, emailAddress, createAddButton());
    }

    void createSectionHeader(String text) {
        Paragraph paragraph = new Paragraph(text);
        paragraph.addClassName("pt-l");
        add(paragraph, 2);
        add(new Hr(), 2);
    }

    private HorizontalLayout createAddButton() {
        final Button addClient = new Button("Add client");
        addClient.setEnabled(false);
        addClient.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        addClient.addClickShortcut(Key.ENTER);

        addClient.addClickListener(c -> {
            if (addressForm.isValid() && binder.writeBeanIfValid(client)) {
                final Example<Client> example = createExample();

                Optional<Client> oldClient = clientRepository.findOne(example);

                if (oldClient.isPresent()) {
                    showDialog(oldClient.get());
                } else {
                    client = clientRepository.save(client);
    
                    Address address = addressForm.getAddress();
                    address.setClient(client);
                    addressRepository.save(address);
    
                    fireEvent(new AddClientEvent(this, client));
                }
            }
        });

        binder.addStatusChangeListener(e -> {
            if (!addressForm.isValid()) {
                addClient.setEnabled(false);
                return;
            }

            addClient.setEnabled(binder.isValid());
        });

        return new HorizontalLayout(addClient);
    }

    private Example<Client> createExample() {
        Client exampleClient = Client.builder()
                .firstName(client.getFirstName())
                .lastName(client.getLastName())
                .mothersName(client.getMothersName())
                .dateOfBirth(client.getDateOfBirth())
                .build();

        final ExampleMatcher exampleMatcher = ExampleMatcher.matchingAll()
                .withMatcher("firstName", ExampleMatcher.GenericPropertyMatchers.ignoreCase())
                .withMatcher("lastName", ExampleMatcher.GenericPropertyMatchers.contains());

        return Example.of(exampleClient, exampleMatcher);
    }

    private void showDialog(Client oldClient) {
        ConfirmDialog dialog = new ConfirmDialog();
        dialog.setHeader("The client already exists");
        dialog.setText(
                "What would you like to do: update client data with new information or keep the existing information intact?");

        dialog.setCancelable(true);
        dialog.addCancelListener(e -> dialog.close());

        dialog.setRejectable(true);
        dialog.setRejectText("Continue with old information");
        dialog.addRejectListener(event -> continueWithOldInformation(oldClient));

        dialog.setConfirmText("Update information");
        dialog.addConfirmListener(event -> updateClientInformation(oldClient));

        dialog.open();
    }

    private void continueWithOldInformation(Client oldClient) {
        fireEvent(new AddClientEvent(this, oldClient));
    }

    private void updateClientInformation(Client oldClient) {
        addressRepository.delete(oldClient.getAddress());
        Address newAddress = addressForm.getAddress();
        newAddress.setClient(oldClient);
        addressRepository.save(newAddress);

        oldClient.setEmailAddress(client.getEmailAddress());
        oldClient.setPhoneNumber(client.getPhoneNumber());
        fireEvent(new AddClientEvent(this, clientRepository.save(oldClient)));
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

