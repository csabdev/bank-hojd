package pillercs.app.vaadin.views.process.approved;

import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import pillercs.app.vaadin.views.MainLayout;
import pillercs.app.vaadin.views.Utils;

@Route(value = "application-approved", layout = MainLayout.class)
@PageTitle("Application approved")
public class ApprovedView extends VerticalLayout {

    public ApprovedView() {
        addClassName("approved-view");
        setSizeFull();

        Button home = new Button("Home");
        home.addClickListener(click -> Utils.routeHome(this));
        home.addClickShortcut(Key.ENTER);

        add(new H1("Congrats, the loan is approved!"),
                new Icon(VaadinIcon.PIGGY_BANK_COIN),
                new H2("The client will receive the money shortly"),
                home);
    }
}
