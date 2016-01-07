package org.srcm.heartfulness.enumeration;

/**
 *  Enumerator class to identify Participant details header values for V3 template.
 * 
 * @author Goutham
 *
 */
public enum V3ParticipantCols {


	SEQUENCE_NUMBER("Sequence Number", 6, 0),
	FIRST_NAME("First Name", 6 , 1),
	MIDDLE_NAME("Middle Name", 6, 2),
	LAST_NAME("Last Name", 6, 3),
	FIRST_SITTING_DATE("1st Sitting Date", 6, 4),
	SECOND_SITTING_DATE("2nd Sitting Date", 6, 5),
	THIRD_SITTING_DATE("3rd Sitting Date", 6, 6),
	MOBILE("Mobile", 6, 7),
	EMAIL_ID("Email ID", 6, 8),
	RESIDENTIAL_AREA("Residential Area", 6, 9),
	CITY("City", 6, 10),
	STATE("State", 6, 11),
	COUNTRY("Country", 6, 12),
	PINCODE("Pincode", 6, 13),
	DATE_OF_BIRTH("Date of Birth", 6, 15),
	PROFESSION("Profession", 6, 16),
	PARTICIPANT_FEEDBACK("Participant Feedback", 6, 23),
	WELCOME_CARD_NUMBER("Welcome Card Number", 6, 24),
	PRECEPTOR_NAME("Preceptor Name", 6, 27),
	PRECEPTOR_ID("Preceptor ID", 6, 28);

	/**Instance to identify the header value for participants in V3 template. */
	private String header;
	/**Instance to identify the row value for participants in V3 template. */
	private int row;
	/**Instance to identify the column value for participants in V3 template. */
	private int cell;

	private V3ParticipantCols(String header,int row, int cell) {
		this.header = header;
		this.row = row;
		this.cell = cell;
	}

	/**
	 * get hold of the participant header value for V3 template.
	 * 
	 * @return the header
	 */
	public String getHeader() {
		return header;
	}

	/**
	 * set the participant header value for V3 template.
	 * 
	 * @param header
	 *  
	 */
	public void setHeader(String header) {
		this.header = header;
	}

	/**
	 * get hold of the participant column value for V3 template.
	 * 
	 * @return the cell
	 */
	public int getCell() {
		return cell;
	}

	/**
	 * set the participant column value for V3 template.
	 * 
	 * @param cell
	 * 
	 */
	public void setCell(int cell) {
		this.cell = cell;
	}

	/**
	 *  get hold of the participant row value for V3 template.
	 * 
	 * @return row value
	 */
	public int getRow() {
		return row;
	}
	/**
	 * set the participant row value for V3 template.
	 * 
	 * @param row
	 */
	public void setRow(int row) {
		this.row = row;
	}

}
