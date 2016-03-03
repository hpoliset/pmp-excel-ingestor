package org.srcm.heartfulness;

import javax.servlet.ServletContext;

import org.springframework.core.annotation.Order;
import org.springframework.web.WebApplicationInitializer;

@Order(value = 1)
public class PmpInitializer implements WebApplicationInitializer {
	@Override
	public void onStartup(ServletContext container) {
		container.setInitParameter("contextConfigLocation", "NOTNULL");
	}
}