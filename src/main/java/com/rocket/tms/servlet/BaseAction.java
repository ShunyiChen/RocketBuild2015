/**
 * 
 */
package com.rocket.tms.servlet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rocket.tms.json.ResponseJsonObj;
import com.rocket.tms.service.ServiceFactory;
import com.rocket.tms.service.TrucoreMSService;

/**
 * @author xxu
 * 
 */
public abstract class BaseAction extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static final Logger logger = LoggerFactory.getLogger(BaseAction.class);

	private HttpServletResponse resp;

	private HttpServletRequest req;

	private int requestCMD;

	protected TrucoreMSService trucoreMSService;
	
	@Override
	public void init() throws ServletException{ 
		trucoreMSService = ServiceFactory.getInstance().getCircleService(this);
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest
	 * , javax.servlet.http.HttpServletResponse)
	 */
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		this.doPost(req, resp);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * javax.servlet.http.HttpServlet#doPost(javax.servlet.http.HttpServletRequest
	 * , javax.servlet.http.HttpServletResponse)
	 */
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		resp.setContentType("text/html; charset=UTF-8");
		// this.requestCMD = Integer.valueOf(req.getParameter("cmd"));
		this.resp = resp;
		this.req = req;
	}
	
	protected String readJSONString(HttpServletRequest request) {
		StringBuffer json = new StringBuffer();
		String line = null;
		try {
			BufferedReader reader = request.getReader();
			while ((line = reader.readLine()) != null) {
				json.append(line);
			}
		} catch (Exception e) {
			return null;
		}
		return json.toString();
	}

	protected int getReqCMD() {
		return requestCMD;
	}

	protected void responseToClient(ResponseJsonObj respJsonObj) throws IOException {
		PrintWriter out = resp.getWriter();
		logger.info("Response to Client json: " + respJsonObj.toSerializable());
		out.print(respJsonObj.toSerializable());
		out.flush();
		out.close();
	}

	
	protected void responseToClient(HttpServletResponse res, ResponseJsonObj respJsonObj) throws IOException {
		PrintWriter out = res.getWriter();
		logger.info("Response to Client json: " + respJsonObj.toSerializable());
		out.print(respJsonObj.toSerializable());
		out.flush();
		out.close();
	}
}
