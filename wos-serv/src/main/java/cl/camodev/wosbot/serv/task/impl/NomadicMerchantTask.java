package cl.camodev.wosbot.serv.task.impl;

import cl.camodev.wosbot.console.enumerable.EnumTpMessageSeverity;
import cl.camodev.wosbot.serv.impl.ServLogs;
import cl.camodev.wosbot.serv.task.ATask;

public class NomadicMerchantTask extends ATask {

	public NomadicMerchantTask(String profile) {
		super(profile);
	}

	@Override
	protected void execute() {
//		ServLogs.getServices().appendLog(EnumTpMessageSeverity.INFO, "["+taskName + "] checking for nomadic merchant offers");
//		try {
//			Thread.sleep(2000); // Simulación de trabajo
//		} catch (InterruptedException e) {
//			Thread.currentThread().interrupt();
//		}
	}
}
