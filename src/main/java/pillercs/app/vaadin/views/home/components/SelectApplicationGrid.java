package pillercs.app.vaadin.views.home.components;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.spring.annotation.SpringComponent;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import pillercs.app.vaadin.data.entity.Application;
import pillercs.app.vaadin.data.enums.Role;
import pillercs.app.vaadin.data.repository.ApplicationRepository;
import pillercs.app.vaadin.services.WorkflowService;

@SpringComponent
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class SelectApplicationGrid extends VerticalLayout {

    private final ApplicationRepository applicationRepository;
    private final WorkflowService workflowService;

    private Grid<Application> grid;
    private Button continueApplication;
    private Button previousPage;
    private Button nextPage;
    private HorizontalLayout footer;

    private final static int PAGE_SIZE = 5;
    private final int maxPage;
    private int currentPage;

    public SelectApplicationGrid(ApplicationRepository applicationRepository,
                                 WorkflowService workflowService) {
        this.applicationRepository = applicationRepository;
        this.workflowService = workflowService;

        setSizeFull();
        maxPage = ((int) this.applicationRepository.count() - 1) / PAGE_SIZE;

        configureButtons();
        configureGrid();
        configureFooter();
        updateGrid();

        add(grid, footer);
    }

    private void configureButtons() {
        continueApplication = new Button("Continue an existing application");
        continueApplication.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        continueApplication.setEnabled(false);
        continueApplication.addClickListener(c ->
                workflowService.currentStep(this, grid.asSingleSelect().getValue().getApplicationId()));

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
        grid.addColumn(application -> application.getState().getName()).setHeader("State");
        grid.getColumns().forEach(col -> col.setAutoWidth(true));

        grid.setAllRowsVisible(true);

        grid.asSingleSelect().addValueChangeListener(e -> continueApplication.setEnabled(e.getValue() != null));
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
