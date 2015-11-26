package com.rocket.tms.json;

import com.rocket.tpa.mfx.JsonHelper;

public class RespDownloadFileJsonObj extends ResponseJsonObj {

	public String sender;
	
	public String fileName;
	
	public String filePath;
	
	public String fileSize;
	
	public String status;
	
	@Override
	public String toSerializable() {
		return JsonHelper.toJSON(this);
	}

}
