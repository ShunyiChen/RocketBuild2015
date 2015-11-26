package com.rocket.tms.servlet;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URLEncoder;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.trubiquity.tpa.router.ServiceException;

import com.rocket.tms.json.ReqGetDownloadFilsJsonObj;
import com.rocket.tms.util.Constant;
import com.rocket.tms.util.JsonHelper;

public class FileDownloadAction extends BaseAction {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Write over doPost() method to retrieve the posted response, download and upload a file to a directory in server.
	 */
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String jsonStr = readJSONString(request);
		// Start uploading files
		try {
			if (jsonStr == null || "".equals(jsonStr)) {
				throw new Exception("JSON data is an empty string or NULL.");
			}
			ReqGetDownloadFilsJsonObj rj = JsonHelper.parseObject(jsonStr,
					ReqGetDownloadFilsJsonObj.class);
			File f = trucoreMSService.fileDownload(rj.token, rj.filename, Constant.DOWNLOAD_FOLDER);

			if(f != null && f.exists()){  
				FileInputStream fis = new FileInputStream(f);  
				String filename= URLEncoder.encode(f.getName(),"utf-8");
				byte[] b = new byte[fis.available()];  
				fis.read(b);
				response.setContentType("application/octet-stream");
				response.setCharacterEncoding("utf-8");
				response.setHeader("Content-Disposition","attachment; filename="+filename+"");
				response.setHeader("filelength", f.length()+"");
				
				ServletOutputStream out = response.getOutputStream();  
				out.write(b); 
				out.flush(); 
				out.close();
				fis.close();
			}     

		} catch (ServiceException e1) {
			e1.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
