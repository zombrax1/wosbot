package cl.camodev.wosbot.almac.repo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cl.camodev.wosbot.almac.entity.Config;
import cl.camodev.wosbot.almac.entity.TpConfig;
import cl.camodev.wosbot.almac.jpa.BotPersistence;
import cl.camodev.wosbot.console.enumerable.TpConfigEnum;

public class ConfigRepository implements IConfigRepository {
    private static ConfigRepository instance;
    private final BotPersistence persistence = BotPersistence.getInstance();

    private ConfigRepository() {}

    public static ConfigRepository getRepository() {
        if (instance == null) {
            instance = new ConfigRepository();
        }
        return instance;
    }

    @Override
    public List<Config> getGlobalConfigs() {
        String q = "SELECT c FROM Config c WHERE c.profile IS NULL";
        return persistence.getQueryResults(q, Config.class, null);
    }

    @Override
    public List<Config> getProfileConfigs(Long profileId) {
        String q = "SELECT c FROM Config c WHERE c.profile.id = :profileId";
        Map<String, Object> p = new HashMap<>();
        p.put("profileId", profileId);
        return persistence.getQueryResults(q, Config.class, p);
    }

    @Override
    public TpConfig getTpConfig(TpConfigEnum type) {
        String q = "SELECT t FROM TpConfig t WHERE t.name = :name";
        Map<String, Object> p = new HashMap<>();
        p.put("name", type.name());
        List<TpConfig> result = persistence.getQueryResults(q, TpConfig.class, p);
        return result.isEmpty() ? null : result.get(0);
    }

    @Override
    public boolean addConfig(Config config) {
        return persistence.createEntity(config);
    }

    @Override
    public boolean saveConfig(Config config) {
        return persistence.updateEntity(config);
    }

    @Override
    public boolean deleteConfig(Config config) {
        return persistence.deleteEntity(config);
    }
}
