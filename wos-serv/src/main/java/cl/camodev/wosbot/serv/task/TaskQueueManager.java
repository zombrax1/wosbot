package cl.camodev.wosbot.serv.task;

import java.util.HashMap;
import java.util.Map;

public class TaskQueueManager {

	private final Map<String, TaskQueue> taskQueues = new HashMap<>();

	public void createQueue(String queueName) {
		if (!taskQueues.containsKey(queueName)) {
			taskQueues.put(queueName, new TaskQueue());
		}
	}

	public TaskQueue getQueue(String queueName) {
		return taskQueues.get(queueName);
	}

	public void startQueue(String queueName) {
		TaskQueue queue = taskQueues.get(queueName);
		if (queue != null) {
			queue.start();
		}
	}

	public void stopQueue(String queueName) {
		TaskQueue queue = taskQueues.get(queueName);
		if (queue != null) {
			queue.stop();
		}
	}
}
