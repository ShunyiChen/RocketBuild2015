package com.rocket.tms.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.rocket.tms.json.ResponseJsonObj;

public class LoginAction extends BaseAction {

	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		super.doPost(request, response);
		ResponseJsonObj respJsonObj = this.trucoreMSService.login(this.readJSONString(request));
		super.responseToClient(respJsonObj);
	}
}
