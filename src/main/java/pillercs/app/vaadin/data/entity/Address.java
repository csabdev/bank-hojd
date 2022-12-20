package pillercs.app.vaadin.data.entity;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@Entity
public class Address extends AbstractEntity {

    @GeneratedValue(generator = "address_seq")
    @Id
    private Long addressId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id")
    private Client client;

    @NotBlank
    @Size(min = 4)
    private String country;

    @NotBlank
    @Size(min = 2)
    private String city;

    @NotBlank
    @Size(min = 4)
    private String postalCode;

    @NotBlank
    @Size(min = 3)
    private String streetNameAndNumber;

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;

        Address other = (Address) obj;

        return addressId != null && addressId.equals(other.getAddressId());
    }

    @Override
    public int hashCode() {
        return this.getClass().hashCode();
    }

    @Override
    public String toString() {
        return "Address{" +
                "addressId=" + addressId +
                ", city='" + city + '\'' +
                ", postalCode='" + postalCode + '\'' +
                ", streetNameAndNumber='" + streetNameAndNumber + '\'' +
                '}';
    }
}
