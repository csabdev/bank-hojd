package pillercs.app.vaadin.views.process.offers;

import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import lombok.Setter;
import pillercs.app.vaadin.data.entity.Offer;
import pillercs.app.vaadin.data.repository.OfferRepository;
import pillercs.app.vaadin.views.MainLayout;
import pillercs.app.vaadin.views.process.approved.ApprovedView;
import pillercs.app.vaadin.views.process.offers.components.OfferDiv;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Route(value = "offers", layout = MainLayout.class)
@PageTitle("Offers")
public class OffersView extends VerticalLayout {

    private final OfferRepository offerRepository;

    private final Button selectOffer;
    private List<OfferDiv> offerDivs;

    @Setter
    Long applicationId;

    public OffersView(OfferRepository offerRepository) {
        this.offerRepository = offerRepository;
        addClassName("offers-view");

        selectOffer = createSelectButton();

        add(selectOffer);
    }

    private Button createSelectButton() {
        Button selectOffer = new Button("Select offer");
        selectOffer.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        selectOffer.addClickShortcut(Key.ENTER);

        selectOffer.setEnabled(false);

        selectOffer.addClickListener(__ -> {
            Offer selectedOffer = null;
            for (OfferDiv div : offerDivs) {
                if (div.hasClassName("selected")) selectedOffer = div.getOffer();
            }

            if (selectedOffer != null) {
                selectedOffer.setAccepted(true);
                this.offerRepository.save(selectedOffer);
                getUI().ifPresent(ui -> ui.navigate(ApprovedView.class));
            } else {
                Notification notification = Notification.show("No offer was selected!");
                notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
                notification.setDuration(5_000);
            }
        });

        return selectOffer;
    }

    public void showOffers() {
        List<Offer> offers = offerRepository.findByUnderwriting_Application_ApplicationId(applicationId);

        offerDivs = offers.stream()
                .sorted(Comparator.comparing(Offer::getOrdinal))
                .limit(3)
                .map(OfferDiv::new)
                .collect(Collectors.toList());

        for (OfferDiv offerDiv : offerDivs) {
            offerDiv.addClickListener(click -> selectOffer((OfferDiv) click.getSource()));
        }

        for (int i = 0; i < 3; i++) {
            addComponentAtIndex(i, offerDivs.get(i));
        }
    }

    private void selectOffer(OfferDiv source) {
        if (source.isSelected()) {
            source.deselect();
            selectOffer.setEnabled(false);
            return;
        }

        for (OfferDiv div : offerDivs) {
            div.deselect();
        }
        source.select();
        selectOffer.setEnabled(true);
    }

}
