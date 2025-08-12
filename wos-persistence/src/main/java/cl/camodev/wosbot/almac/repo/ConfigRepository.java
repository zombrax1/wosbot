package cl.camodev.wosbot.almac.repo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cl.camodev.wosbot.almac.entity.Config;
import cl.camodev.wosbot.almac.entity.TpConfig;
import cl.camodev.wosbot.console.enumerable.TpConfigEnum;

import cl.camodev.wosbot.almac.jpa.BotPersistence;

public class ConfigRepository implements IConfigRepository {
        private static ConfigRepository instance;

        public static ConfigRepository getRepository() {
                if (instance == null) {
                        instance = new ConfigRepository();
                }
                return instance;
        }

        private BotPersistence getPersistence(Long profileId) {
                return BotPersistence.getInstance(String.valueOf(profileId));
        }

	@Override
	public boolean addConfig(Config config) {
                Long profileId = config.getProfile() != null ? config.getProfile().getId() : null;
                BotPersistence persistence = profileId != null ? getPersistence(profileId) : BotPersistence.getInstance();
                return persistence.createEntity(config);
	}

	@Override
	public boolean saveConfig(Config config) {
                Long profileId = config.getProfile() != null ? config.getProfile().getId() : null;
                BotPersistence persistence = profileId != null ? getPersistence(profileId) : BotPersistence.getInstance();
                return persistence.updateEntity(config);
	}

	@Override
	public boolean deleteConfig(Config config) {
                Long profileId = config.getProfile() != null ? config.getProfile().getId() : null;
                BotPersistence persistence = profileId != null ? getPersistence(profileId) : BotPersistence.getInstance();
                return persistence.deleteEntity(config);
	}

        @Override
        public Config getConfigById(Long profileId, Long id) {
                return getPersistence(profileId).findEntityById(Config.class, id);
        }

	@Override
	public List<Config> getProfileConfigs(Long profileId) {
		String query = "SELECT c FROM Config c WHERE c.profile.id = :profileId";
		Map<String, Object> parameters = new HashMap<>();
		parameters.put("profileId", profileId);
                return getPersistence(profileId).getQueryResults(query, Config.class, parameters);
	}

	@Override
	public List<Config> getGlobalConfigs() {
		String query = "SELECT c FROM Config c WHERE c.profile IS NULL";
                return BotPersistence.getInstance().getQueryResults(query, Config.class, null);
	}

	@Override
	public TpConfig getTpConfig(TpConfigEnum tpConfigEnum) {
                return BotPersistence.getInstance().findEntityById(TpConfig.class, tpConfigEnum.getId());
}

}
