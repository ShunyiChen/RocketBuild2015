/**
 * 
 */
package com.rocket.tms.service;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServlet;

import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

/**
 * @author xxu
 *
 */
public class ServiceFactory {

	private WebApplicationContext getApplicationContext(HttpServlet httpServlet) {
		ServletContext servletContext = httpServlet.getServletContext();
		return WebApplicationContextUtils.getWebApplicationContext(servletContext);
	}

	public TrucoreMSService getCircleService(HttpServlet httpServlet) {
		return (TrucoreMSService) this.getApplicationContext(httpServlet).getBean("trucoreMSServiceImpl");
	}

	private static ServiceFactory instance = null;

	private ServiceFactory() {
	}

	public static ServiceFactory getInstance() {
		if (instance == null)
			instance = new ServiceFactory();
		return instance;
	}
}
