package pillercs.app.vaadin.views;


import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.theme.lumo.LumoUtility;
import pillercs.app.vaadin.components.appnav.AppNav;
import pillercs.app.vaadin.components.appnav.AppNavItem;

public class MainLayout extends AppLayout {

    private H2 viewTitle;

    public MainLayout() {
        setPrimarySection(Section.DRAWER);
        addDrawerContent();
        addHeaderContent();
    }

    private void addHeaderContent() {
        DrawerToggle toggle = new DrawerToggle();
        toggle.getElement().setAttribute("aria-label", "Menu toggle");

        viewTitle = new H2();
        viewTitle.addClassNames(LumoUtility.FontSize.LARGE, LumoUtility.Margin.NONE);

        addToNavbar(true, toggle, viewTitle);
    }

    private void addDrawerContent() {
        Image logo = new Image("images/logo_s.svg", "Logo of the Bank");

        Header header = new Header(logo);
        header.addClassName("main-drawer-header");

        Scroller scroller = new Scroller(createNavigation());

        addToDrawer(header, scroller, createFooter());
    }

    private AppNav createNavigation() {
        // AppNav is not yet an official component.
        // For documentation, visit https://github.com/vaadin/vcf-nav#readme
        AppNav nav = new AppNav();

        nav.addItem(new AppNavItem("Home", HomeView.class, "la la-home"));

//        nav.addItem(new AppNavItem("List", ListView.class, "la la-th"));
//        nav.addItem(new AppNavItem("Person Form", PersonFormView.class, "la la-user"));
//        nav.addItem(new AppNavItem("Address Form", AddressFormView.class, "la la-map-marker"));
//        nav.addItem(new AppNavItem("Checkout Form", CheckoutFormView.class, "la la-credit-card"));
//        nav.addItem(new AppNavItem("Dashboard", DashboardView.class, "la la-chart-area"));
//        nav.addItem(new AppNavItem("Image List", ImageListView.class, "la la-th-list"));

        return nav;
    }

    private Footer createFooter() {
        Footer layout = new Footer();

        return layout;
    }

    @Override
    protected void afterNavigation() {
        super.afterNavigation();
        viewTitle.setText(getCurrentPageTitle());
    }

    private String getCurrentPageTitle() {
        PageTitle title = getContent().getClass().getAnnotation(PageTitle.class);
        return title == null ? "" : title.value();
    }
}
