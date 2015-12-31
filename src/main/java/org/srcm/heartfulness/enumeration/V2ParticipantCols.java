package org.srcm.heartfulness.enumeration;

/**
 *  Enumerator class to identify Participant details header values for v2.1 template.
 * 
 * @author Koustav Dutta
 *
 */
public enum V2ParticipantCols {
	
	NAME("Name*", 0, 0), 
	FIRST_SITTING("1st Sitting\n(Y/N/Date)", 0, 1), 
	SECONND_SITTING("2nd Sitting\n(Y/N/Date)", 0, 2), 
	THIRD_SITTING("3rd Sitting \n(Y/N/Date)", 0, 3),
	COUNTRY("Country*", 0, 4),
	STATE("State*", 0, 5),
	CITY("City*", 0, 6),
	EMAIL("Email ID", 0, 7),
	MOBILE("Mobile", 0, 8),
	PROFFESION("Profession", 0, 9),
	DEPARTMENT("Department / Stream Name", 0, 10),
	BATCH_YEAR("Batch / Year", 0, 11),
	RECEIVE_UPDATES("Receive Updates\n(Y/N)", 0, 12),
	GENDER("Gender", 0, 13),
	AGE_GROUP("Age Group", 0, 14),
	PREF_LANGUAGE("Preferred language for Communication", 0, 15),
	WELCOME_CARD_NUMBER("Welcome Card "+"\n"+"Number (Issued after 3rd sittings)", 0, 16),
	WELCOME_CARD_ISSUE_DATE("Welcome Card\nIssued Date", 0, 17),
	REMARKS("Remarks", 0, 18);
	
	/**Instance to identify the header value for participants in v2.1 template. */
	private String header;
	/**Instance to identify the row value for participants in v2.1 template. */
	private int row;
	/**Instance to identify the column value for participants in v2.1 template. */
	private int cell;

	private V2ParticipantCols(String header,int row, int cell) {
		this.header = header;
		this.row = row;
		this.cell = cell;
	}

	/**
	 * get hold of the participant header value for v2.1 template.
	 * 
	 * @return the header
	 */
	public String getHeader() {
		return header;
	}

	/**
	 * set the participant header value for v2.1 template.
	 * 
	 * @param header
	 *  
	 */
	public void setHeader(String header) {
		this.header = header;
	}

	/**
	 * get hold of the participant column value for v2.1 template.
	 * 
	 * @return the cell
	 */
	public int getCell() {
		return cell;
	}

	/**
	 * set the participant column value for v2.1 template.
	 * 
	 * @param cell
	 * 
	 */
	public void setCell(int cell) {
		this.cell = cell;
	}
	
	/**
	 *  get hold of the participant row value for v2.1 template.
	 * 
	 * @return row value
	 */
	public int getRow() {
		return row;
	}
	/**
	 * set the participant row value for v2.1 template.
	 * 
	 * @param row
	 */
	public void setRow(int row) {
		this.row = row;
	}

}
