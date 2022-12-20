package pillercs.app.vaadin.views.process.selectclient.components;

import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.renderer.LocalDateRenderer;
import com.vaadin.flow.shared.Registration;
import com.vaadin.flow.spring.annotation.SpringComponent;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import pillercs.app.vaadin.data.entity.Client;
import pillercs.app.vaadin.data.repository.ClientRepository;

@SpringComponent
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class SingleSelectClientGrid extends VerticalLayout {

    private final ClientRepository clientRepository;
    private final Grid<Client> clientGrid = new Grid<>(Client.class, false);
    private final TextField firstName = new TextField("First name");
    private final TextField lastName = new TextField("Last name");
    private final Button select = new Button("Select client");
    private final Button search = new Button("Search");

    public SingleSelectClientGrid(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;

        setWidth("95%");
        configureClientGrid();

        search.addClickShortcut(Key.ENTER);
        search.addClickListener(e -> searchClient());

        final HorizontalLayout searchFields = new HorizontalLayout(firstName, lastName, search);
        searchFields.setAlignItems(Alignment.END);

        select.setEnabled(false);
        select.addClickListener(event -> fireEvent(new SelectEvent(this, clientGrid.asSingleSelect().getValue())));

        add(searchFields, clientGrid, select);
    }

    public void configureClientGrid() {
        clientGrid.addClassName("search-client-grid");
        clientGrid.setHeight("300px");

        clientGrid.addColumn("firstName").setHeader("First name");
        clientGrid.addColumn("lastName").setHeader("Last name");
        clientGrid.addColumn(new LocalDateRenderer<>(Client::getDateOfBirth, "dd/MM/yyyy")).setHeader("Date of birth");
        clientGrid.addColumn("mothersName").setHeader("Mother's name");

        clientGrid.getColumns().forEach(col -> col.setAutoWidth(true));
        clientGrid.asSingleSelect().addValueChangeListener(selectionEvent -> select.setEnabled(selectionEvent.getValue() != null));
    }

    private void searchClient() {
        final String firstNameValue = firstName.getValue();
        final String lastNameValue = lastName.getValue();

        final Client client = Client.builder()
                .firstName("".equals(firstNameValue) ? null : firstNameValue)
                .lastName("".equals(lastNameValue) ? null : lastNameValue)
                .build();

        final ExampleMatcher exampleMatcher = ExampleMatcher.matchingAll()
                .withMatcher("firstName", ExampleMatcher.GenericPropertyMatchers.contains().ignoreCase())
                .withMatcher("lastName", ExampleMatcher.GenericPropertyMatchers.contains().ignoreCase());

        final Example<Client> example = Example.of(client, exampleMatcher);

        clientGrid.setItems(clientRepository.findAll(example));
    }

    public <T extends ComponentEvent<?>> Registration addListener(Class<T> eventType,
                                                                  ComponentEventListener<T> listener) {
        return getEventBus().addListener(eventType, listener);
    }

    public static class SelectEvent extends ComponentEvent<SingleSelectClientGrid> {

        private final Client client;

        public SelectEvent(SingleSelectClientGrid source, Client client) {
            super(source, false);
            this.client = client;
        }

        public Client getClient() { return client; }
    }
}
