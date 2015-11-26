package com.rocket.tms.json;

import com.rocket.tpa.mfx.JsonHelper;

public class RespPartner extends ResponseJsonObj {
	public String username;

	public String company;
	
	public String firstname;
	
	public String lastname;
	
	public String email;

	public RespPartner() {
	}

	@Override
	public String toSerializable() {
		return JsonHelper.toJSON(this);
	}
}