package org.srcm.heartfulness.enumeration;

/**
 * Enumerator class to identify Event details header values for v2.1 template.
 * 
 * @author Koustav Dutta
 *
 */
public enum V2ProgramCols {

	EVENT_TYPE("Event Type*", 0, 2, 150), 
	OTHER("Other", 2, 2, 255), 
	EVENT_PLACE("Event Place*", 0, 3, 150), 
	EVENT_DATE("Event Date (DD-MMM-YY)*", 2, 3, 255), 
	EVENT_COUNTRY("Event Country*", 0, 4, 50), 
	EVENT_STATE("Event State*", 2, 4, 150),
	EVENT_CITY("Event City*", 0, 5, 150), 
	EVENT_COORDINATORNAME("Event Coordinator Name*", 0, 6, 150), 
	EVENT_COORDINATOR_MOBILE("Event Coordinator Mobile*", 0, 7, 150), 
	EVENT_COORDINATOR_MAIL("Event Coordinator Email ID*", 2, 7, 150), 
	ORGANIZATION_NAME("Organisation Name*", 0, 9, 150), 
	ORGANIZATION_CONTACT_PERSON("Organisation Contact Person*", 2, 9, 100),
	ORGANIZATION_WEBSITE("Organisation Website", 0, 10, 150), 
	ORGANIZATION_CONTACT_MAILID("Organisation Contact Email ID*", 2, 10, 100), 
	ORGANIZATION_CONTACT_MOBILE("Organisation Contact Mobile*", 2, 11, 25), 
	PRECEPTOR_NAME("Preceptor Name*", 0, 13, 45), 
	WELCOME_CARD_SIGNEDBY("Welcome Card Signed By", 2, 13, 45), 
	PRECEPTOR_ID("Preceptor ID*", 0, 14, 45), 
	WELCOME_CARD_SIGNER_ID("Welcome Card Signer's ID", 2, 14, 45), 
	REMARKS("Remarks:", 0, 16, 500);

	/**Instance to identify the header value for participants in v2.1 template. */
	private String header;
	/**Instance to identify the cell value for participants in v2.1 template. */
	private int cell;
	/**Instance to identify the row value for participants in v2.1 template. */
	private int row;
	/**Instance to identify the length value for participants in v2.1 template. */
	private int length;

	private V2ProgramCols(String header, int cell, int row, int length) {
		this.header = header;
		this.cell = cell;
		this.row = row;
		this.length = length;
	}

	/**
	 *  get hold of the event row value for v2.1 template.
	 * 
	 * @return row value
	 */
	public int getRow() {
		return row;
	}

	/**
	 * set the event row value for v2.1 template.
	 * 
	 * @param row
	 */
	public void setRow(int row) {
		this.row = row;
	}

	/**
	 * get hold of the event header value for v2.1 template.
	 * 
	 * @return the header
	 */
	public String getHeader() {
		return header;
	}

	/**
	 * set the event header value for v2.1 template.
	 * 
	 * @param header
	 *  
	 */
	public void setHeader(String header) {
		this.header = header;
	}

	/**
	 * get hold of the event column value for v2.1 template.
	 * 
	 * @return the cell
	 */
	public int getCell() {
		return cell;
	}

	/**
	 * set the event column value for v2.1 template.
	 * 
	 * @param cell
	 * 
	 */
	public void setCell(int cell) {
		this.cell = cell;
	}
	
	/**
	 *  get hold of the event length value for v2.1 template.
	 * 
	 * @return row value
	 */
	public int getLength() {
		return length;
	}

	/**
	 * set the event length value for v2.1 template.
	 * 
	 * @param row
	 */
	public void setLength(int length) {
		this.length = length;
	}
}
