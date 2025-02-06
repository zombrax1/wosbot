package cl.camodev.wosbot.serv.task;

public abstract class ATask implements Runnable {
	protected String taskName;

	public ATask(String taskName) {
		this.taskName = taskName;
	}

	@Override
	public void run() {
		execute();
	}

	protected abstract void execute();
}