package org.srcm.heartfulness.enumeration;

/**
 * Enumerator class to verify Event details header values for v1.0 template.
 * 
 * @author Goutham
 *
 */
public enum V1ProgramCols {

	EVENT_TYPE("Program Name", 3, 0), 
	OTHER("Other", 3, 4),
	EVENT_COORDINATORNAME("Coordinator's name", 4, 0), 
	EVENT_COORDINATOR_MAIL("Email Id", 5, 0),
	CENTER_NAME("Name of the Center", 6, 0),
	EVENT_STATE("State", 7, 0),
	EVENT_COUNTRY("Country", 8, 0),
	INSTITUTION_NAME("Name of the Institution", 9, 0),
	WEBSITE("Website", 10, 0),
	PROGRAM_DATE("Dates of the program", 11, 0);

	private String header;
	private int cell;
	private int row;

	private V1ProgramCols(String header, int row, int cell) {
		this.header = header;
		this.row = row;
		this.cell = cell;
	}

	/**
	 * @return the row
	 */
	public int getRow() {
		return row;
	}

	/**
	 * @param row
	 *            the row to set
	 */
	public void setRow(int row) {
		this.row = row;
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

	/**
	 * @return the cell
	 */
	public int getCell() {
		return cell;
	}

	/**
	 * @param cell
	 *            the cell to set
	 */
	public void setCell(int cell) {
		this.cell = cell;
	}


}
