package org.srcm.heartfulness.enumeration;

/**
 * Enumerator class to identify Event details header values for V3 template.
 * 
 * @author Goutham
 *
 */
public enum V3ProgramCols {

	EVENT_COORDINATORNAME("Event Coordinator Name", 1, 1), 
	EVENT_COORDINATOR_MOBILE("Event Coordinator Mobile", 2, 1), 
	EVENT_COORDINATOR_MAIL("Event Coordinator Email ID", 3, 1),
	EVENT_COORDINATOR_ID("Event Coordinator ID Card Number", 4, 1),
	ORGANIZATION_CONTACT_PERSON("Organisation Contact Person", 1, 3),
	ORGANIZATION_CONTACT_PERSON_MOBILE("Organisation Contact Person Mobile", 2, 3),
	ORGANIZATION_CONTACT_MAILID("Organisation Contact Person Email ID", 3, 3),
	ORGANIZATION_NAME("Organisation Name", 1, 5),
	ORGANISATION_CONTACT_NUMBER("Organisation Contact Number", 2, 5),
	ORGANIZATION_WEBSITE("Organisation Website", 3, 5),
	DEPARTMENT("Department / Stream Name", 4, 5),
	EVENT_PLACE("Event Place", 1 ,7),
	EVENT_CITY("Event City", 2, 7),
	EVENT_STATE("Event State", 3, 7),
	EVENT_TYPE("Event Type", 1 , 9),
	EVENT_COUNTRY("Event Country", 2, 9),
	EVENT_DATE("Event Date", 3, 9),
	EVENT_ID("Event ID", 4, 9);

	/**Instance to identify the header value for event in V3 template. */
	private String header;
	/**Instance to identify the row value for event in V3 template. */
	private int row;
	/**Instance to identify the cell value for event in V3 template. */
	private int cell;


	private V3ProgramCols(String header, int row, int cell) {
		this.header = header;
		this.row = row;
		this.cell = cell;
	}

	/**
	 *  get hold of the event row value for V3 template.
	 * 
	 * @return row value
	 */
	public int getRow() {
		return row;
	}

	/**
	 * set the event row value for V3 template.
	 * 
	 * @param row
	 */
	public void setRow(int row) {
		this.row = row;
	}

	/**
	 * get hold of the event header value for V3 template.
	 * 
	 * @return the header
	 */
	public String getHeader() {
		return header;
	}

	/**
	 * set the event header value for V3 template.
	 * 
	 * @param header
	 *  
	 */
	public void setHeader(String header) {
		this.header = header;
	}

	/**
	 * get hold of the event column value for V3 template.
	 * 
	 * @return the cell
	 */
	public int getCell() {
		return cell;
	}

	/**
	 * set the event column value for V3 template.
	 * 
	 * @param cell
	 * 
	 */
	public void setCell(int cell) {
		this.cell = cell;
	}


}
