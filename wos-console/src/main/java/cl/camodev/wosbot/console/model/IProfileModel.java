package cl.camodev.wosbot.console.model;

import java.util.List;

import cl.camodev.wosbot.ot.DTOConfig;
import cl.camodev.wosbot.ot.OTProfiles;

public interface IProfileModel {

	public List<OTProfiles> getProfiles();

	public void startBot(List<DTOConfig> configs);

}
