package pillercs.app.vaadin.views;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import pillercs.app.vaadin.data.entity.Application;
import pillercs.app.vaadin.data.enums.Role;
import pillercs.app.vaadin.data.repository.ApplicationRepository;

import static pillercs.app.vaadin.utils.GeneralConst.BANK_NAME;

@PageTitle(BANK_NAME)
@Route(value = "", layout = MainLayout.class)
public class HomeView extends VerticalLayout {

    private final static int PAGE_SIZE = 5;
    private final ApplicationRepository applicationRepository;
    private int maxPage;
    private int currentPage;

    private H1 welcome;
    private Paragraph text;
    private Button newApplication;
    private Button continueApplication;
    private Button previousPage;
    private Button nextPage;
    private Grid<Application> grid;
    private HorizontalLayout footer;

    public HomeView(ApplicationRepository applicationRepository) {
        this.applicationRepository = applicationRepository;

        configureLayout();
        configureButtons();
        configureGrid();
        configureFooter();
        updateGrid();

        add(welcome, text, newApplication, grid, footer);
    }

    private void configureLayout() {
        welcome = new H1("Welcome User!");
        text = new Paragraph("What would you like to do?");
        setSizeFull();
        maxPage = ((int) applicationRepository.count() - 1) / PAGE_SIZE;
    }

    private void configureButtons() {
        newApplication = new Button("Create a new application");
        newApplication.setDisableOnClick(true);
        newApplication.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        newApplication.addClickListener(c -> newApplication.getUI().ifPresent(ui ->
                ui.navigate(ClientView.class)));

        continueApplication = new Button("Continue an existing application");
        continueApplication.setEnabled(false);

        previousPage = new Button("Previous page");
        previousPage.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        previousPage.setEnabled(false);
        previousPage.addClickListener(c -> {
            currentPage -= 1;
            updateGrid();
            nextPage.setEnabled(true);
            if (currentPage == 0) previousPage.setEnabled(false);
        });

        nextPage = new Button("Next page");
        nextPage.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        if (currentPage >= maxPage) nextPage.setEnabled(false);
        nextPage.addClickListener(c -> {
            currentPage += 1;
            updateGrid();
            previousPage.setEnabled(true);
            if (currentPage == maxPage) nextPage.setEnabled(false);
        });
    }

    private void configureGrid() {
        grid = new Grid<>(Application.class, false);
        grid.addClassName("search-application-grid");

        grid.addColumn("applicationId").setHeader("Application Id");
        grid.addColumn("createdByUser").setHeader("Created by");
        grid.addColumn(a -> a.getCreated().toLocalDate()).setHeader("Created on");
        grid.addColumn(app -> app.getApplicants()
                .stream()
                .filter(appl -> Role.DEBTOR == appl.getRole())
                .map(appl -> appl.getClient().getFirstName() + " " + appl.getClient().getLastName())
                .findAny().orElseThrow()).setHeader("Debtor");
        grid.getColumns().forEach(col -> col.setAutoWidth(true));

        grid.setAllRowsVisible(true);

        grid.asSingleSelect().addValueChangeListener(e -> continueApplication.setEnabled(e.getValue() != null));
        grid.addClassName("mt-auto");
    }

    private void configureFooter() {
        footer = new HorizontalLayout(continueApplication, previousPage, nextPage);
        continueApplication.addClassName("mr-auto");
        nextPage.addClassName("mr-auto");
        footer.setWidth("100%");
    }

    private void updateGrid() {
        grid.setItems(applicationRepository
                .findAllWithApplicants(PageRequest.of(currentPage, PAGE_SIZE, Sort.Direction.DESC, "applicationId")));
    }

}
