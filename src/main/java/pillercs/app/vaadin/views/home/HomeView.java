package pillercs.app.vaadin.views.home;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import pillercs.app.vaadin.views.MainLayout;
import pillercs.app.vaadin.views.home.components.SelectApplicationGrid;
import pillercs.app.vaadin.views.process.selectclient.SelectClientView;

import static pillercs.app.vaadin.utils.GeneralConst.BANK_NAME;

@PageTitle(BANK_NAME + " - Cash loan application")
@Route(value = "", layout = MainLayout.class)
public class HomeView extends VerticalLayout {

    private final H1 welcome = new H1("Welcome User!");
    private final Paragraph introduction = new Paragraph("What would you like to do?");
    private final Button newApplication = new Button("Create new application");
    private final Button continueApplication = new Button("Continue application");
    private final Button backToNewApplication = new Button("Create a new application instead");

    private final VerticalLayout newApplicationSection;
    private final VerticalLayout selectApplicationSection;

    public HomeView(SelectApplicationGrid selectApplicationGrid) {
        addClassName("home-view");
        setWidth("95%");
        configureButtons();

        newApplicationSection = new VerticalLayout(welcome, introduction, newApplication, continueApplication);
        selectApplicationSection = new VerticalLayout(new H1("Choose the application that you wish to continue"), backToNewApplication, selectApplicationGrid);
        add(newApplicationSection);
    }

    private void configureButtons() {
        newApplication.setWidth("200px");
        newApplication.setDisableOnClick(true);
        newApplication.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        newApplication.addClickListener(c -> newApplication());

        continueApplication.setWidth("200px");
        continueApplication.addClickListener(c -> {
            remove(newApplicationSection);
            add(selectApplicationSection);
        });

        backToNewApplication.addClickListener(c -> newApplication());
    }

    private void newApplication() {
        this.getUI().ifPresent(ui ->
                ui.navigate(SelectClientView.class));
    }

}
