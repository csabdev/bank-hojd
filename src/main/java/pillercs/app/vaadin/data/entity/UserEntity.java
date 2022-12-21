package pillercs.app.vaadin.data.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;

@Entity(name = "users")
@Getter
@Setter
public class UserEntity {

    @Id
    private String username;

    @NotNull
    private String password;

    @NotNull
    private int enabled;

}
