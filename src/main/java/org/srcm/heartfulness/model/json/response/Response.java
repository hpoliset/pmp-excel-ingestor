package org.srcm.heartfulness.model.json.response;

import java.util.Arrays;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * This class holds the success response for the web service endpoints.
 * 
 * @author HimaSree
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Response {

	private String status;
	private String description;
	private byte[] byteDescription;

	public Response() {
		super();
	}
	
	public Response(String status, byte[] byteDescription) {
		super();
		this.status = status;
		this.byteDescription = byteDescription;
	}

	public Response(String status, String description) {
		super();
		this.status = status;
		this.description = description;
	}
	
	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public byte[] getByteDescription() {
		return byteDescription;
	}

	public void setByteDescription(byte[] byteDescription) {
		this.byteDescription = byteDescription;
	}

	@Override
	public String toString() {
		return "Response [status=" + status + ", description=" + description + ", byteDescription="
				+ Arrays.toString(byteDescription) + "]";
	}


}
