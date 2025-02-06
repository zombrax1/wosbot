package cl.camodev.wosbot.serv.impl;

import cl.camodev.wosbot.console.enumerable.EnumTpMessageSeverity;
import cl.camodev.wosbot.console.list.ILogListener;

public class ServLogs {

	private static ServLogs instance;

	private ILogListener iLogListener;

	private ServLogs() {

	}

	public static ServLogs getServices() {
		if (instance == null) {
			instance = new ServLogs();
		}
		return instance;
	}

	public void setLogListener(ILogListener listener) {
		this.iLogListener = listener;
	}

	public void appendLog(EnumTpMessageSeverity severity, String message) {
		if (iLogListener != null) {
			iLogListener.onLogReceived(severity, message);
		}
	}
}
