/**
 * 
 */
package org.srcm.heartfulness.enumeration;

/**
 * @author Koustav Dutta
 *
 */
public enum MobileDataParticipantCols {

	ID("ID", 0, 0, 255),
	NAME("Name", 1, 0, 150),
	FIRST_SITTING("1st Sitting", 2, 0, 11),
	SECONND_SITTING("2nd Sitting", 3, 0, 11),
	THIRD_SITTING("3rd Sitting", 4, 0, 11),
	STATE("State", 5, 0, 50),
	CITY("City", 6, 0, 50),
	EMAIL("Email ID", 7, 0, 250),
	MOBILE("Mobile", 8, 0, 25),
	RECEIVE_UPDATES("Receive Updates", 9, 0, 1),
	GENDER("Gender", 10, 0, 10),
	AGE_GROUP("Age Group", 11, 0, 45),
	PREF_LANGUAGE("Preferred language for Communication", 12, 0, 45),
	TOTAL_DAYS("Total Days", 13, 0, 2),
	REMARKS("Remarks", 14, 0, 500);


	private String header;
	private int cell;
	private int row;
	private int length;

	private MobileDataParticipantCols(String header, int cell, int row, int length) {
		this.header = header;
		this.cell = cell;
		this.row = row;
		this.length = length;
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
	
	public int getLength() {
		return length;
	}

	public void setLength(int length) {
		this.length = length;
	}

}
