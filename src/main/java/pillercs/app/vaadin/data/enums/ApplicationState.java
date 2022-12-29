package pillercs.app.vaadin.data.enums;

import lombok.Getter;

public enum ApplicationState {
    CREATED("created"),
    RECORD_BASIC_INFORMATION("Recording basic information"),
    RECORD_INCOME("Recording income"),
    RECORD_APPLICANT_DETAILS("Recording applicant details"),
    UNDERWRITING_IN_PROGRESS("Underwriting in progress"),
    UNDERWRITING_RESULTS("Underwriting results"),
    OFFERS("Offers"),
    CONTRACT("contract"),
    APPROVED("Approved"),
    DECLINED("Declined");

    @Getter
    private final String name;

    ApplicationState(String name) {
        this.name = name;
    }
}
