package org.srcm.heartfulness.util;

import java.io.PrintWriter;
import java.io.StringWriter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Stack Trace conversion utilities.
 * 
 * @author himasreev
 *
 */
public class StackTraceUtils {

	public static String convertStackTracetoString(Exception exception) {
		StringWriter stack = new StringWriter();
		exception.printStackTrace(new PrintWriter(stack));
		return stack.toString();
	}

	public static String convertPojoToJson(Object obj) {
		ObjectMapper mapper = new ObjectMapper();
		try {
			return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(obj);
		} catch (JsonProcessingException e) {
			return StackTraceUtils.convertStackTracetoString(e);
		}
	}

}
