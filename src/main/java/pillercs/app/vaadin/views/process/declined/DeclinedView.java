package pillercs.app.vaadin.views.process.declined;

import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import pillercs.app.vaadin.views.MainLayout;
import pillercs.app.vaadin.views.Utils;

@Route(value = "declined", layout = MainLayout.class)
@PageTitle("Application declined")
public class DeclinedView extends VerticalLayout {

    public DeclinedView() {
        addClassName("declined-view");
        setWidth("95%");

        final Button home = new Button("Home");
        home.addClickListener(click -> Utils.routeHome(this));
        home.addClickShortcut(Key.ENTER);

        final Span icon = new Span();
        icon.setClassName("la-la-exclamation-triangle");

        add(new H1("Sorry, but the application was declined!"),
//                icon,
                new Icon(VaadinIcon.EXCLAMATION_CIRCLE),
                home);
    }
}
