package pillercs.app.vaadin.security;

import com.vaadin.flow.spring.security.VaadinWebSecurity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import pillercs.app.vaadin.views.login.LoginView;

import javax.sql.DataSource;
import java.util.Collections;

@EnableWebSecurity 
@Configuration
public class SecurityConfig extends VaadinWebSecurity {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .antMatchers("/images/**")
                .permitAll()
                .antMatchers("/h2-console/**").permitAll()
                .and().csrf().ignoringAntMatchers("/h2-console/**")
                .and().headers().frameOptions().sameOrigin();

        super.configure(http);

        setLoginView(http, LoginView.class);
    }

    @Autowired
    @Bean
    public JdbcUserDetailsManager userDetailService(DataSource dataSource) {
        JdbcUserDetailsManager manager = new JdbcUserDetailsManager(dataSource);
        manager.createUser(new User("admin",
                "{bcrypt}$2a$10$cRqfrdolNVFW6sAju0eNEOE0VC29aIyXwfsEsY2Fz2axy3MnH8ZGa",
                Collections.singleton(new SimpleGrantedAuthority("ROLE_ADMIN"))
        ));
        manager.createUser(new User("user",
                "{bcrypt}$2a$10$cRqfrdolNVFW6sAju0eNEOE0VC29aIyXwfsEsY2Fz2axy3MnH8ZGa",
                Collections.singleton(new SimpleGrantedAuthority("ROLE_USER"))
        ));
        manager.createUser(new User("Csaba",
                "{bcrypt}$2a$10$cRqfrdolNVFW6sAju0eNEOE0VC29aIyXwfsEsY2Fz2axy3MnH8ZGa",
                Collections.singleton(new SimpleGrantedAuthority("ROLE_USER"))
        ));
        return manager;
    }
}