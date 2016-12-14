package org.srcm.heartfulness.enumeration;

/**
 * Enumeration to identify the participant table column names used to search participant
 * details of an event in Heartfulness.
 * 
 * @author himasreev
 *
 */
public enum ParticipantSearchField {
	
	printName("print_name"),
	gender("gender"),
	addressLine1("address_line1"),
	addressLine2("address_line2"),
	email("email"),
	mobilePhone("mobile_phone"),
	city("city"),
	state("state"),
	country("country"),
	introducedStatus("introduced"),
	introducedBy("introduced_by"),
	abhyasiId("abhyasi_id");
	
	String value;
	
	private ParticipantSearchField() {
	}

	private ParticipantSearchField(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

}
