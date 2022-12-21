package pillercs.app.vaadin.data.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Entity(name = "authorities")
@Getter
@Setter
public class AuthoritiesEntity implements Serializable {

    @Id
    private String username;

    @NotNull
    private String authority;

}
