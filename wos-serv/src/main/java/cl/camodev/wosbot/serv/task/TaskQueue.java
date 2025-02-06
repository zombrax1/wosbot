package cl.camodev.wosbot.serv.task;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

public class TaskQueue {
	private final BlockingQueue<ATask> taskQueue = new LinkedBlockingQueue<>();
	private ExecutorService executorService;
	private volatile boolean running = false;

	public TaskQueue() {
		executorService = Executors.newSingleThreadExecutor();
	}

	public void addTask(ATask task) {
		taskQueue.offer(task);
	}

	public void start() {
		if (running)
			return;
		running = true;

		executorService.submit(() -> {
			while (running) {
				try {
					ATask task = taskQueue.take();
					task.run();
					taskQueue.offer(task);
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
				}
			}
		});
	}

	public void stop() {
		running = false;
		executorService.shutdownNow();
	}
}
