package cl.camodev.wosbot.console.model.impl;

import java.util.List;

import cl.camodev.wosbot.console.model.IProfileModel;
import cl.camodev.wosbot.ot.OTProfiles;
import cl.camodev.wosbot.serv.impl.ServProfiles;

public class ProfileModel implements IProfileModel {

	@Override
	public List<OTProfiles> getProfiles() {
		return ServProfiles.getServices().getProfiles();
	}

}
