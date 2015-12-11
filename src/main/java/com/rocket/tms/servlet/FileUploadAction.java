package com.rocket.tms.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;


import net.trubiquity.tpa.router.ServiceException;

import org.apache.commons.codec.binary.Base64;
import org.json.JSONException;

import com.rocket.tms.json.ReqFileUploadJsonObj;
import com.rocket.tms.json.ResFileUploadJsonObj;
import com.rocket.tms.json.ResponseJsonObj;
import com.rocket.tms.service.TrucoreMSService;
import com.rocket.tms.util.Constant;
import com.rocket.tms.util.JsonHelper;

public class FileUploadAction extends BaseAction {

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * Write over doPost() method to retrieve the posted response, encrypt and upload a file to a directory in server.
	 */
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		ResponseJsonObj respJsonObj = new ResFileUploadJsonObj();
		OutputStream out = null;
		String filePath = getServletConfig().getServletContext().getRealPath("/"); 
		String jsonStr = readJSONString(request);
		try {
			if (jsonStr == null || "".equals(jsonStr)) {
				throw new Exception("JSON data is an empty string or NULL.");
			}
			ReqFileUploadJsonObj rj = JsonHelper.parseObject(jsonStr, ReqFileUploadJsonObj.class);
	        byte[] decodedBytes = Base64.decodeBase64(rj.image.getBytes());
			File p = new File(filePath+"//"+Constant.TC_UPLOAD_FOLDER);
			if (!p.exists()) {
				p.mkdir();
			}
			File uploadFile = new File(p.getAbsolutePath()+"//"+rj.filename);
			if (!uploadFile.exists()) {
				uploadFile.createNewFile();
			}
			out = new FileOutputStream(uploadFile);
		    out.write(decodedBytes);  
			// Add uploadfile to the list.
			List<File> lstFile = new ArrayList<File>();
			lstFile.add(uploadFile);
			
			// Start uploading
			respJsonObj = trucoreMSService.fileUpload(rj.token, lstFile, filePath + Constant.TC_UPLOAD_FOLDER, rj.pfirstname, rj.plastname, rj.pemail);
		} catch (JSONException e2) {
			response.setStatus(400);
			respJsonObj.result = TrucoreMSService.FAILED;
			respJsonObj.log = e2.getMessage();
			e2.printStackTrace();
		} catch (ServiceException e1) {
			response.setStatus(400);
			respJsonObj.result = TrucoreMSService.FAILED;
			respJsonObj.log = e1.getMessage();
			e1.printStackTrace();
		} catch (Exception e) {
			response.setStatus(400);
			respJsonObj.result = TrucoreMSService.FAILED;
			respJsonObj.log = e.getMessage();
			e.printStackTrace();
		} finally {
			if (out != null) {
				out.close();
			}
			// Return the messages to the client.
			responseToClient(response, respJsonObj);
		}
	}
}
