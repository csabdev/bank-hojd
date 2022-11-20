package pillercs.app.vaadin.views.process.offers.components;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Paragraph;
import lombok.Getter;
import pillercs.app.vaadin.data.entity.Offer;

import java.text.DecimalFormat;

public class OfferDiv extends Div {

    @Getter
    private final Offer offer;

    private static final String SELECTED = "selected";

    public OfferDiv(Offer offer) {
        this.offer = offer;

        addClassName("offer");
        add(new H3("Loan amount: " + formatNumber(offer.getLoanAmount())));
        add(new Paragraph("Term: " + offer.getTerm() +
                ", Monthly instalment: " + formatNumber(offer.getMonthlyInstalment())));
    }

    private String formatNumber(Integer i) {
        DecimalFormat myFormatter = new DecimalFormat();
        return myFormatter.format(i);
    }

    public void select() { addClassName(SELECTED); }

    public void deselect() { removeClassName(SELECTED); }

    public boolean isSelected() { return hasClassName(SELECTED); }
}
