package cl.camodev.wosbot.almac.repo;

import java.util.List;

import cl.camodev.wosbot.almac.entity.Profile;
import cl.camodev.wosbot.almac.jpa.BotPersistence;
import cl.camodev.wosbot.ot.OTProfiles;

public class ProfileRepository implements IProfileRepository {

	private static ProfileRepository instance;

	private BotPersistence persistence = BotPersistence.getInstance();

	private ProfileRepository() {
	}

	public static ProfileRepository getRepository() {
		if (instance == null) {
			instance = new ProfileRepository();
		}
		return instance;
	}

	@Override
	public List<OTProfiles> getProfiles() {
		Profile p = new Profile();
		p.setEmulatorNumber(1L);
		p.setName("Camolo");
		p.setEnabled(true);
		persistence.createEntity(p);
		return null;
	}

}
