package org.wos.launcher.controller;

import java.util.List;

import cl.camodev.wosbot.console.model.IProfileModel;
import cl.camodev.wosbot.console.model.impl.ProfileModel;
import cl.camodev.wosbot.ot.OTProfiles;

public class LauncherActionController {

	private IProfileModel iModel;

	public LauncherActionController() {
		iModel = new ProfileModel();
	}

	public List<OTProfiles> getProfiles() {
		return iModel.getProfiles();
	}

}
