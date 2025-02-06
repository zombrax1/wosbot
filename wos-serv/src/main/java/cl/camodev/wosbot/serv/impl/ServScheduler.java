package cl.camodev.wosbot.serv.impl;

import java.util.List;

import cl.camodev.wosbot.ot.DTOConfig;
import cl.camodev.wosbot.serv.task.TaskQueueManager;
import cl.camodev.wosbot.serv.task.impl.InitializeTask;
import cl.camodev.wosbot.serv.task.impl.NomadicMerchantTask;

public class ServScheduler {
	private static ServScheduler instance;

	private final TaskQueueManager queueManager = new TaskQueueManager();

	private ServScheduler() {

	}

	public static ServScheduler getServices() {
		if (instance == null) {
			instance = new ServScheduler();
		}
		return instance;
	}

	public void startBot(List<DTOConfig> configs) {

		for (DTOConfig config : configs) {
			String queueName = config.getProfileName() + "_" + config.getEmulatorNumber();

			queueManager.createQueue(queueName);
			queueManager.getQueue(queueName).addTask(new InitializeTask(queueName));

			if (config.getNomadicMerchant().booleanValue()) {
				NomadicMerchantTask nomadicMerchantTask = new NomadicMerchantTask(queueName);
				queueManager.getQueue(queueName).addTask(nomadicMerchantTask);
			}

			queueManager.startQueue(queueName);
		}

	}

}
