package cl.camodev.wosbot.console.list;

import cl.camodev.wosbot.console.enumerable.EnumTpMessageSeverity;

public interface ILogListener {

	void onLogReceived(EnumTpMessageSeverity severity, String message);

}
