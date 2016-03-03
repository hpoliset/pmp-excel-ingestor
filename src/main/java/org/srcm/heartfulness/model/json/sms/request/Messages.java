package org.srcm.heartfulness.model.json.sms.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 
 * @author gouthamc
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Messages {

	@JsonProperty(value="Text")
	private String Text;

	@JsonProperty(value="Number")
	private String Number;

	public String getText() {
		return Text;
	}

	public void setText(String Text) {
		this.Text = Text;
	}

	public String getNumber() {
		return Number;
	}

	public void setNumber(String Number) {
		this.Number = Number;
	}

	@Override
	public String toString() {
		return "ClassPojo [Text = " + Text + ", Number = " + Number + "]";
	}
}
