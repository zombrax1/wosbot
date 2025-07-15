package cl.camodev.wosbot.serv.impl;

import java.util.HashMap;
import java.util.List;

import cl.camodev.wosbot.almac.entity.Config;
import cl.camodev.wosbot.almac.repo.ConfigRepository;
import cl.camodev.wosbot.almac.repo.IConfigRepository;

public class ServConfig {

	private static ServConfig instance;

	private IConfigRepository iConfigRepository = ConfigRepository.getRepository();

	private ServConfig() {

	}

	public static ServConfig getServices() {
		if (instance == null) {
			instance = new ServConfig();
		}
		return instance;
	}

	public HashMap<String, String> getGlobalConfig() {
		int maxRetries = 3;
		int retryCount = 0;
		
		while (retryCount < maxRetries) {
			try {
				List<Config> configs = iConfigRepository.getGlobalConfigs();

				if (configs == null || configs.isEmpty()) {
					return new HashMap<>();
				}

				HashMap<String, String> globalConfig = new HashMap<>();
				for (Config config : configs) {
					globalConfig.put(config.getKey(), config.getValor());
				}
				return globalConfig;
				
			} catch (Exception e) {
				retryCount++;
				if (retryCount >= maxRetries) {
					System.err.println("Failed to get global config after " + maxRetries + " retries: " + e.getMessage());
					// Return empty map instead of null to prevent NPE
					return new HashMap<>();
				}
				// Wait before retrying
				try {
					Thread.sleep(1000 * retryCount); // Exponential backoff
				} catch (InterruptedException ie) {
					Thread.currentThread().interrupt();
					return new HashMap<>();
				}
			}
		}
		
		return new HashMap<>();
	}

}
