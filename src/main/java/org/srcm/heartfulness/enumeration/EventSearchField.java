package org.srcm.heartfulness.enumeration;

/**
 * Enumeration to identify the program table column names used to search event
 * details of Heartfulness.
 * 
 * @author himasreev
 *
 */
public enum EventSearchField {

	programChannel("program_channel"),
	coordinatorName("coordinator_name"),
	coordinatorEmail("coordinator_email"),
	coordinatorMobile("coordinator_mobile"),
	eventPlace("event_place"),
	eventCity("event_city"),
	eventState("event_state"),
	eventCountry("event_country"),
	organizationDepartment("organization_department"),
	organizationName("organization_name"),
	organizationWebSite("organization_web_site"),
	organizationContactName("organization_contact_name"),
	organizationContactEmail("organization_contact_email"),
	organizationContactMobile("organization_contact_mobile"),
	preceptorName("preceptor_name"),
	preceptorIdCardNumber("preceptor_id_card_number"),
	welcomeCardSignedByName("welcome_card_signed_by_name"),
	welcomeCardSignerIdCardNumber("welcome_card_signer_id_card_number");

	String value;

	private EventSearchField() {
	}

	private EventSearchField(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

}
