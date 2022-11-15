package pillercs.app.vaadin.data.entity;

import lombok.*;
import pillercs.app.vaadin.data.enums.Gender;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.time.LocalDate;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@Entity
public class Client extends AbstractEntity {

    @GeneratedValue(generator = "client_seq")
    @Id
    private Long clientId;

    @NotBlank
    @Size(min = 3)
    private String firstName;

    @NotBlank
    @Size(min = 3)
    private String lastName;

    @Past
    @NotNull
    private LocalDate dateOfBirth;

    @NotBlank
    @Size(min = 3)
    private String mothersName;

    @NotNull
    @Enumerated(EnumType.STRING)
    private Gender gender;

    @OneToOne(mappedBy = "client", cascade = CascadeType.REMOVE)
    private Address address;

    @NotBlank
    private String phoneNumber;

    @NotBlank
    @Email
    private String emailAddress;

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;

        Client other = (Client) obj;

        return clientId != null && clientId.equals(other.getClientId());
    }

    @Override
    public int hashCode() {
        return this.getClass().hashCode();
    }

    @Override
    public String toString() {
        return "Client{" +
                "clientId=" + clientId +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", dateOfBirth=" + dateOfBirth +
                ", mothersName='" + mothersName + '\'' +
                ", gender=" + gender +
                ", address=" + address +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", emailAddress='" + emailAddress + '\'' +
                '}';
    }
}
