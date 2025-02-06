package cl.camodev.wosbot.serv.task.impl;

import cl.camodev.wosbot.console.enumerable.EnumTpMessageSeverity;
import cl.camodev.wosbot.emulator.EmulatorManager;
import cl.camodev.wosbot.serv.impl.ServLogs;
import cl.camodev.wosbot.serv.task.ATask;

public class InitializeTask extends ATask {
	boolean isStarted = false;

	public InitializeTask(String taskName) {
		super(taskName);
	}

	@Override
	protected void execute() {
		ServLogs.getServices().appendLog(EnumTpMessageSeverity.INFO, "[" + taskName + "] initializing bot");
		while (!isStarted) {

			boolean playerState = EmulatorManager.getPlayerState(taskName.split("_")[1]);

			if (playerState) {
				isStarted = true;
				ServLogs.getServices().appendLog(EnumTpMessageSeverity.INFO,
						"[" + taskName + "] emulator has finished starting");
			} else {
				ServLogs.getServices().appendLog(EnumTpMessageSeverity.INFO, "[" + taskName + "] emulator not found");
				EmulatorManager.launchPlayer(taskName.split("_")[1]);
				ServLogs.getServices().appendLog(EnumTpMessageSeverity.INFO,
						"[" + taskName + "] waiting 30 seconds before checking again");
				try {
					Thread.sleep(30000);
				} catch (Exception e) {

				}
			}

		}

		if (!EmulatorManager.isWhiteoutSurvivalInstalled(taskName.split("_")[1])) {
			ServLogs.getServices().appendLog(EnumTpMessageSeverity.ERROR,
					"[" + taskName + "] whiteout survival not installed");
		} else {
			ServLogs.getServices().appendLog(EnumTpMessageSeverity.INFO,
					"[" + taskName + "] whiteout survival installed");

		}

	}

}
