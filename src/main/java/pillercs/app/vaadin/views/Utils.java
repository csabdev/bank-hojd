package pillercs.app.vaadin.views;

import com.vaadin.flow.component.Component;
import pillercs.app.vaadin.data.entity.Income;
import pillercs.app.vaadin.views.home.HomeView;

import java.util.List;

public final class Utils {

    public static int calculateMonthlyInstalment(double interestRate, int presentValue, int requestedTerm) {
        return (int) Math.ceil(interestRate * presentValue / (1 - Math.pow(1 + interestRate, -1 * requestedTerm)));
    }

    public static <T extends Component> void routeHome(T source) {
        source.getUI().ifPresent(ui -> ui.navigate(HomeView.class));
    }

    public static double sumIncomes(List<Income> incomes) {
        return incomes.stream()
                .reduce(0d,
                        (result, income) -> result + (income.getAmount() * income.getType().getFrequency().getMultiplier()),
                        Double::sum);
    }

}
