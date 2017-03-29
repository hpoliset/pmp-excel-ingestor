/**
 * 
 */
package org.srcm.heartfulness.enumeration;

/**
 * @author Koustav Dutta
 *
 */
public enum MobileDataParticipantCols {

	NAME("Name", 1, 0),
	FIRST_SITTING("1st Sitting", 2, 0),
	SECONND_SITTING("2nd Sitting", 3, 0),
	THIRD_SITTING("3rd Sitting", 4, 0),
	STATE("State", 5, 0),
	CITY("City", 6, 0),
	EMAIL("Email ID", 7, 0),
	MOBILE("Mobile", 8, 0),
	RECEIVE_UPDATES("Receive Updates", 9, 0),
	GENDER("Gender", 10, 0),
	AGE_GROUP("Age Group", 11, 0),
	PREF_LANGUAGE("Preferred language for Communication", 12, 0),
	TOTAL_DAYS("Total Days", 13, 0),
	REMARKS("Remarks", 14, 0);


	private String header;
	private int cell;
	private int row;

	private MobileDataParticipantCols(String header, int cell, int row) {
		this.header = header;
		this.cell = cell;
		this.row = row;
	}

	public String getHeader() {
		return header;
	}

	public void setHeader(String header) {
		this.header = header;
	}

	public int getCell() {
		return cell;
	}

	public void setCell(int cell) {
		this.cell = cell;
	}

	public int getRow() {
		return row;
	}

	public void setRow(int row) {
		this.row = row;
	}

}
