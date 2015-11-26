package com.rocket.tms.json;

import com.rocket.tpa.mfx.JsonHelper;

public class ResFileUploadJsonObj extends ResponseJsonObj {

	public String token;

	@Override
	public String toSerializable() {
		return JsonHelper.toJSON(this);
	}
}
