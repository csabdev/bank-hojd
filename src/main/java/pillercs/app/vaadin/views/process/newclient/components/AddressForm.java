package pillercs.app.vaadin.views.process.newclient.components;

import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.spring.annotation.SpringComponent;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import pillercs.app.vaadin.data.entity.Address;

@SpringComponent
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class AddressForm extends FormLayout {

    private final Binder<Address> binder = new BeanValidationBinder<>(Address.class);

    private final TextField country = new TextField("Country");
    private final TextField city = new TextField("City");
    private final TextField postalCode = new TextField("Postal code");
    private final TextField streetNameAndNumber = new TextField("Street name and number");

    private Address address = new Address();

    public AddressForm() {
        addClassName("address-form");

        binder.bindInstanceFields(this);

        add(country, city, postalCode, streetNameAndNumber);
    }

    public boolean isValid() {
        return binder.isValid();
    }

    public Address getAddress() {
        if (binder.writeBeanIfValid(address)) {
            return address;
        }

        return null;
    }

}
