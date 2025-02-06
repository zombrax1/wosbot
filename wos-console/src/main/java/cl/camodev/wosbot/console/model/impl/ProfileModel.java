package cl.camodev.wosbot.console.model.impl;

import java.util.List;

import cl.camodev.wosbot.console.model.IProfileModel;
import cl.camodev.wosbot.ot.DTOConfig;
import cl.camodev.wosbot.ot.OTProfiles;
import cl.camodev.wosbot.serv.impl.ServProfiles;
import cl.camodev.wosbot.serv.impl.ServScheduler;

public class ProfileModel implements IProfileModel {

	@Override
	public List<OTProfiles> getProfiles() {
		return ServProfiles.getServices().getProfiles();
	}

	@Override
	public void startBot(List<DTOConfig> configs) {
		ServScheduler.getServices().startBot(configs);
		
	}

}
