package pillercs.app.vaadin.views;

import com.vaadin.flow.component.Component;

public final class Utils {

    public static int calculateMonthlyInstalment(double interestRate, int presentValue, int requestedTerm) {
        return (int) Math.ceil(interestRate * presentValue / (1 - Math.pow(1 + interestRate, -1 * requestedTerm)));
    }

    public static <T extends Component> void routeHome(T source) {
        source.getUI().ifPresent(ui -> ui.navigate(HomeView.class));
    }

}
