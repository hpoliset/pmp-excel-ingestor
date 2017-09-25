package org.srcm.heartfulness.enumeration;

/**
 * Enumerator class to verify Event details header values for v1.0 template.
 * 
 * @author Goutham
 *
 */
public enum V1ProgramCols {

	EVENT_TYPE("Program Name", 3, 0, 150), 
	OTHER("Other", 3, 4, 255),
	EVENT_COORDINATORNAME("Coordinator's name", 4, 0, 150), 
	EVENT_COORDINATOR_MAIL("Email Id", 5, 0, 150),
	CENTER_NAME("Name of the Center", 6, 0, 150),
	EVENT_STATE("State", 7, 0, 150),
	EVENT_COUNTRY("Country", 8, 0, 50),
	INSTITUTION_NAME("Name of the Institution", 9, 0, 150),
	WEBSITE("Website", 10, 0, 150),
	PROGRAM_DATE("Dates of the program", 11, 0, 11);

	/**Instance to identify the header value for event in v1.0 template. */
	private String header;
	/**Instance to identify the cell value for event in v1.0 template. */
	private int cell;
	/**Instance to identify the row value for event in v1.0 template. */
	private int row;
	/**Instance to identify the row value for event in v1.0 template. */
	private int length;

	private V1ProgramCols(String header, int row, int cell, int length) {
		this.header = header;
		this.row = row;
		this.cell = cell;
		this.length = length;
	}

	/**
	 *  get hold of the event row value for v1.0 template.
	 * 
	 * @return row value
	 */
	public int getRow() {
		return row;
	}

	/**
	 * set the event row value for v1.0 template.
	 * 
	 * @param row
	 */
	public void setRow(int row) {
		this.row = row;
	}

	/**
	 * get hold of the event header value for v1.0 template.
	 * 
	 * @return the header
	 */
	public String getHeader() {
		return header;
	}

	/**
	 * set the event header value for v1.0 template.
	 * 
	 * @param header
	 *  
	 */
	public void setHeader(String header) {
		this.header = header;
	}

	/**
	 * get hold of the event column value for v1.0 template.
	 * 
	 * @return the cell
	 */
	public int getCell() {
		return cell;
	}

	/**
	 * set the event column value for v1.0 template.
	 * 
	 * @param cell
	 * 
	 */
	public void setCell(int cell) {
		this.cell = cell;
	}
	
	/**
	 *  get hold of the length row value for v1.0 template.
	 * 
	 * @return row value
	 */
	public int getLength() {
		return length;
	}
	
	/**
	 * set the length row value for v1.0 template.
	 * 
	 * @param row
	 */
	public void setLength(int length) {
		this.length = length;
	}
}
