package org.srcm.heartfulness.enumeration;

/**
 * Enumerator class to verify Participant details header values for v1.0 template.
 * 
 * @author Koustav Dutta
 *
 */
public enum V1ParticipantCols {

	S_NO("S. No.", 14, 0),
	FULL_NAME("Full Name" + "\n" + "(First Name and Last Name)", 14, 1),
	CITY("City" ,14, 2),
	STATE("State", 14, 3),
	EMAIL_ADDRESS("Email Address", 14, 4),
	PHONE("Phone #", 14, 5),
	OCCUPATION("Occupation", 14, 6),
	INTRODUCED("Introduced (Completed 3 sittings)"+ "\n" + "(Yes / No)", 14, 7),
	INTRODUCED_DATE("Introduced Date" + "\n" + "(dd/mm/yyyy)", 14, 8),
	INTRODUCED_BY("Introduced By", 14, 9),
	REMARKS("Remarks",14, 10);

	private String header;
	private int row;
	private int cell;

	private V1ParticipantCols(String header,int row, int cell) {
		this.header = header;
		this.row = row;
		this.cell = cell;
	}

	/**
	 * @return the header
	 */
	public String getHeader() {
		return header;
	}

	/**
	 * @param header
	 *            the header to set
	 */
	public void setHeader(String header) {
		this.header = header;
	}

	public int getRow() {
		return row;
	}

	public void setRow(int row) {
		this.row = row;
	}

	/**
	 * @return the column
	 */
	public int getCell() {
		return cell;
	}

	/**
	 * @param column
	 *            the column to set
	 */
	public void setCell(int cell) {
		this.cell = cell;
	}

}
