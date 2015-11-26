package com.rocket.tms.json;

import java.util.ArrayList;

import com.rocket.tpa.mfx.JsonHelper;

public class RespGetDownloadFilesJsonObj extends ResponseJsonObj{

	public ArrayList<RespDownloadFileJsonObj> downloadFiles = new ArrayList<RespDownloadFileJsonObj>();
	
	@Override
	public String toSerializable() {
		return JsonHelper.toJSON(this);
	}

}
