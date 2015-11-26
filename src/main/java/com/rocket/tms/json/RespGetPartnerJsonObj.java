package com.rocket.tms.json;

import java.util.ArrayList;

import com.rocket.tpa.mfx.JsonHelper;

public class RespGetPartnerJsonObj extends ResponseJsonObj {

	public ArrayList<RespPartner> partners = new ArrayList<RespPartner>();

	@Override
	public String toSerializable() {
		return JsonHelper.toJSON(this);
	}
}
