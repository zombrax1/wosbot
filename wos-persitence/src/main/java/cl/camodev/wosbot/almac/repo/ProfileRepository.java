package cl.camodev.wosbot.almac.repo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import cl.camodev.wosbot.almac.entity.Config;
import cl.camodev.wosbot.almac.entity.Profile;
import cl.camodev.wosbot.almac.jpa.BotPersistence;
import cl.camodev.wosbot.ot.DTOConfig;
import cl.camodev.wosbot.ot.DTOProfiles;

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
	public List<DTOProfiles> getProfiles() {
		String queryProfiles = "SELECT new cl.camodev.wosbot.ot.DTOProfiles(p.id, p.name, p.emulatorNumber, p.enabled) FROM Profile p";

		// Obtener perfiles usando getQueryResults
		List<DTOProfiles> profiles = persistence.getQueryResults(queryProfiles, DTOProfiles.class, null);

		if (profiles == null || profiles.isEmpty()) {
			// Crear perfil por defecto si no existen
			Profile defaultProfile = new Profile();
			defaultProfile.setName("Default");
			defaultProfile.setEmulatorNumber("0");
			defaultProfile.setEnabled(true);

			persistence.createEntity(defaultProfile);

			// Reintentar obtener los perfiles
			profiles = persistence.getQueryResults(queryProfiles, DTOProfiles.class, null);
		}

		List<Long> profileIds = profiles.stream().map(DTOProfiles::getId).collect(Collectors.toList());

		if (!profileIds.isEmpty()) {
			// Consulta para obtener las configuraciones de los perfiles
			String queryConfigs = "SELECT new cl.camodev.wosbot.ot.DTOConfig(c.profile.id, c.key, c.valor) " + "FROM Config c WHERE c.profile.id IN :profileIds";

			// Pasar parámetros a la consulta
			Map<String, Object> parameters = new HashMap<>();
			parameters.put("profileIds", profileIds);

			// Agrupar configuraciones por ID de perfil
			List<DTOConfig> configs = persistence.getQueryResults(queryConfigs, DTOConfig.class, parameters);
			Map<Long, List<DTOConfig>> configMap = configs.stream().collect(Collectors.groupingBy(DTOConfig::getProfileId));

			// Asignar configuraciones a los perfiles
			profiles.forEach(profile -> profile.setConfigs(configMap.getOrDefault(profile.getId(), new ArrayList<>())));
		}

		return profiles;
	}

	@Override
	public boolean addProfile(Profile profile) {
		return persistence.createEntity(profile);
	}

	@Override
	public boolean saveProfile(Profile profile) {
		return persistence.updateEntity(profile);
	}

	@Override
	public boolean deleteProfile(Profile profile) {
		return persistence.deleteEntity(profile);
	}

	@Override
	public Profile getProfileById(Long id) {
		if (id == null) {
			return null;
		}
		return persistence.findEntityById(Profile.class, id);
	}

	@Override
	public List<Config> getProfileConfigs(Long profileId) {
		if (profileId == null) {
			return Collections.emptyList();
		}

		String queryStr = "SELECT c FROM Config c WHERE c.profile.id = :profileId";

		// Crear el mapa de parámetros
		Map<String, Object> parameters = new HashMap<>();
		parameters.put("profileId", profileId);

		// Ejecutar la consulta usando getQueryResults()
		return persistence.getQueryResults(queryStr, Config.class, parameters);
	}

	@Override
	public boolean deleteConfigs(List<Config> configs) {
		if (configs == null || configs.isEmpty()) {
			return false;
		}

		try {
			configs.forEach(config -> persistence.deleteEntity(config));
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public boolean saveConfigs(List<Config> configs) {
		if (configs == null || configs.isEmpty()) {
			return false;
		}

		try {
			configs.forEach(config -> persistence.createEntity(config));
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

}
