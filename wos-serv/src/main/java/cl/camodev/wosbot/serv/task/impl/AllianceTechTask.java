package cl.camodev.wosbot.serv.task.impl;

import java.time.LocalDateTime;

import cl.camodev.wosbot.console.enumerable.EnumConfigurationKey;
import cl.camodev.wosbot.console.enumerable.EnumTemplates;
import cl.camodev.wosbot.console.enumerable.EnumTpMessageSeverity;
import cl.camodev.wosbot.console.enumerable.TpDailyTaskEnum;
import cl.camodev.wosbot.emulator.EmulatorManager;
import cl.camodev.wosbot.ot.DTOImageSearchResult;
import cl.camodev.wosbot.ot.DTOPoint;
import cl.camodev.wosbot.ot.DTOProfiles;
import cl.camodev.wosbot.serv.impl.ServLogs;
import cl.camodev.wosbot.serv.impl.ServScheduler;
import cl.camodev.wosbot.serv.task.DelayedTask;

public class AllianceTechTask extends DelayedTask {

	private final EmulatorManager emulator = EmulatorManager.getInstance();
	private final ServLogs logs = ServLogs.getServices();

	public AllianceTechTask(DTOProfiles profile, TpDailyTaskEnum tpDailyTask) {
		super(profile, tpDailyTask);
	}

	@Override
	protected void execute() {

		// Verificar si estamos en HOME o en WORLD
		boolean isHomeOrWorld = emulator.searchTemplate(EMULATOR_NUMBER, EnumTemplates.GAME_HOME_FURNACE.getTemplate(), 0, 0, 720, 1280, 90).isFound() || emulator.searchTemplate(EMULATOR_NUMBER, EnumTemplates.GAME_HOME_WORLD.getTemplate(), 0, 0, 720, 1280, 90).isFound();

		if (!isHomeOrWorld) {
			logs.appendLog(EnumTpMessageSeverity.WARNING, taskName, profile.getName(), "Home not found");
			emulator.tapBackButton(EMULATOR_NUMBER);
			return;
		}

		logs.appendLog(EnumTpMessageSeverity.INFO, taskName, profile.getName(), "Going to alliance chest");

		// Ir a la sección de cofres de alianza
		emulator.tapAtRandomPoint(EMULATOR_NUMBER, new DTOPoint(493, 1187), new DTOPoint(561, 1240));
		sleepTask(3000);

		DTOImageSearchResult menuResult = emulator.searchTemplate(EMULATOR_NUMBER, EnumTemplates.ALLIANCE_TECH_BUTTON.getTemplate(), 0, 0, 720, 1280, 90);
		if (!menuResult.isFound()) {
			this.reschedule(LocalDateTime.now().plusHours(profile.getConfig(EnumConfigurationKey.ALLIANCE_TECH_OFFSET_INT, Integer.class)));
			return;
		}

		emulator.tapAtRandomPoint(EMULATOR_NUMBER, menuResult.getPoint(), menuResult.getPoint());
		sleepTask(500);

		// search for thumb up button

		DTOImageSearchResult thumbUpResult = emulator.searchTemplate(EMULATOR_NUMBER, EnumTemplates.ALLIANCE_TECH_THUMB_UP.getTemplate(), 0, 0, 720, 1280, 90);

		if (!thumbUpResult.isFound()) {
			this.reschedule(LocalDateTime.now().plusHours(profile.getConfig(EnumConfigurationKey.ALLIANCE_TECH_OFFSET_INT, Integer.class)));
			return;
		}

		emulator.tapAtRandomPoint(EMULATOR_NUMBER, thumbUpResult.getPoint(), thumbUpResult.getPoint());

		sleepTask(500);

		emulator.tapAtRandomPoint(EMULATOR_NUMBER, new DTOPoint(450, 1000), new DTOPoint(580, 1050), 25, 100);
		emulator.tapBackButton(EMULATOR_NUMBER);
		emulator.tapBackButton(EMULATOR_NUMBER);
		emulator.tapBackButton(EMULATOR_NUMBER);
		
		LocalDateTime nextSchedule = LocalDateTime.now().plusHours(profile.getConfig(EnumConfigurationKey.ALLIANCE_TECH_OFFSET_INT, Integer.class));
		this.reschedule(nextSchedule);
		ServScheduler.getServices().updateDailyTaskStatus(profile, tpTask, nextSchedule);

	}

}
