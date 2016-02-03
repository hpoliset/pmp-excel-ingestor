package org.srcm.heartfulness;

import javax.servlet.ServletContext;

import org.springframework.core.annotation.Order;
import org.springframework.web.WebApplicationInitializer;

/**
 * This class is to load the application context in order
 * 
 * @author ramesh
 *
 */
@Order(value = 1)
public class PmpInitializer implements WebApplicationInitializer {
	@Override
	public void onStartup(ServletContext container) {
		container.setInitParameter("contextConfigLocation", "NOTNULL");
	}
}