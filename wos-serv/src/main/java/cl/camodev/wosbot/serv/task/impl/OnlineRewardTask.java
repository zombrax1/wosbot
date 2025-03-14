package cl.camodev.wosbot.serv.task.impl;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

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
import net.sourceforge.tess4j.TesseractException;

public class OnlineRewardTask extends DelayedTask {

	private final EmulatorManager emuManager = EmulatorManager.getInstance();
	private final ServLogs servLogs = ServLogs.getServices();

	public OnlineRewardTask(DTOProfiles profile, TpDailyTaskEnum tpDailyTask) {
		super(profile, tpDailyTask);
	}

	@Override
	protected void execute() {
		DTOImageSearchResult homeResult = emuManager.searchTemplate(EMULATOR_NUMBER, EnumTemplates.GAME_HOME_FURNACE.getTemplate(), 0, 0, 720, 1280, 90);
		DTOImageSearchResult worldResult = emuManager.searchTemplate(EMULATOR_NUMBER, EnumTemplates.GAME_HOME_WORLD.getTemplate(), 0, 0, 720, 1280, 90);

		if (homeResult.isFound() || worldResult.isFound()) {
			if (worldResult.isFound()) {
				emuManager.tapAtPoint(EMULATOR_NUMBER, worldResult.getPoint());
				sleepTask(3000);
				servLogs.appendLog(EnumTpMessageSeverity.INFO, taskName, profile.getName(), "Going to storehouse");
			}

			emuManager.tapAtRandomPoint(EMULATOR_NUMBER, new DTOPoint(3, 513), new DTOPoint(26, 588));
			sleepTask(2000);

			EmulatorManager.getInstance().tapAtPoint(EMULATOR_NUMBER, new DTOPoint(110, 270));
			sleepTask(1000);

			emuManager.tapAtRandomPoint(EMULATOR_NUMBER, new DTOPoint(20, 250), new DTOPoint(200, 280));
			sleepTask(2000);

			DTOImageSearchResult researchCenter = emuManager.searchTemplate(EMULATOR_NUMBER, EnumTemplates.GAME_HOME_SHORTCUTS_RESEARCH_CENTER.getTemplate(), 0, 0, 720, 1280, 90);

			if (researchCenter.isFound()) {
				{
					emuManager.tapAtRandomPoint(EMULATOR_NUMBER, researchCenter.getPoint(), researchCenter.getPoint());
					sleepTask(2000);
					emuManager.tapAtRandomPoint(EMULATOR_NUMBER, new DTOPoint(30, 430), new DTOPoint(50, 470));
					sleepTask(2000);

					DTOImageSearchResult chest = null;
					System.out.println("Searching for chest");
					servLogs.appendLog(EnumTpMessageSeverity.INFO, taskName, profile.getName(), "Searching for chest");
					for (int i = 0; i < 5; i++) {
						chest = emuManager.searchTemplate(EMULATOR_NUMBER, EnumTemplates.STOREHOUSE_CHEST.getTemplate(), 0, 0, 720, 1280, 90);

						if (chest.isFound()) {
							// debo obtener la recompensa y verificar caundo es la proxima via ocr
							System.out.println("Chest found, tapping");
							servLogs.appendLog(EnumTpMessageSeverity.INFO, taskName, profile.getName(), "Claiming chest");
							emuManager.tapAtRandomPoint(EMULATOR_NUMBER, chest.getPoint(), chest.getPoint());
							sleepTask(2000);

							emuManager.tapBackButton(EMULATOR_NUMBER);
							for (int j = 0; j < 5; j++) {
								DTOImageSearchResult stamina = emuManager.searchTemplate(EMULATOR_NUMBER, EnumTemplates.STOREHOUSE_STAMINA.getTemplate(), 0, 0, 720, 1280, 90);

								if (stamina.isFound()) {
									servLogs.appendLog(EnumTpMessageSeverity.INFO, taskName, profile.getName(), "Claiming stamina");
									emuManager.tapAtRandomPoint(EMULATOR_NUMBER, stamina.getPoint(), stamina.getPoint());
									sleepTask(2000);
									emuManager.tapAtRandomPoint(EMULATOR_NUMBER, new DTOPoint(250, 930), new DTOPoint(450, 950));
									sleepTask(6000);
									break;
								} else {
									System.out.println("Stamina not found, sleeping");
									sleepTask(100);
								}
							}

							break;
						} else {
							// debo verrificar solo depues de los 5 intento si exsite la stamina, si no hacer ocr para verificar el proximo reward
							System.out.println("Chest not found, sleeping");
							sleepTask(100);
						}

					}

					if (!chest.isFound()) {
						System.out.println("Chest not found, verifying stamina");
						for (int i = 0; i < 5; i++) {
							DTOImageSearchResult stamina = emuManager.searchTemplate(EMULATOR_NUMBER, EnumTemplates.STOREHOUSE_STAMINA.getTemplate(), 0, 0, 720, 1280, 90);

							if (stamina.isFound()) {
								servLogs.appendLog(EnumTpMessageSeverity.INFO, taskName, profile.getName(), "Claiming stamina");
								emuManager.tapAtRandomPoint(EMULATOR_NUMBER, stamina.getPoint(), stamina.getPoint());
								sleepTask(2000);
								emuManager.tapAtRandomPoint(EMULATOR_NUMBER, new DTOPoint(250, 930), new DTOPoint(450, 950));
								sleepTask(6000);
								break;
							} else {
								System.out.println("Stamina not found, sleeping");
								sleepTask(100);
							}
						}
					}

					// debo hacer ocr para verificar el proximo reward

					try {
						String nextRewardTime = emuManager.ocrRegionText(EMULATOR_NUMBER, new DTOPoint(285, 642), new DTOPoint(430, 666));
						System.out.println("Next reward time: " + nextRewardTime);
						servLogs.appendLog(EnumTpMessageSeverity.INFO, taskName, profile.getName(), "Next reward time: " + nextRewardTime);
						LocalDateTime nextReward = parseNextReward(nextRewardTime);
						this.reschedule(nextReward.minusSeconds(5));
						ServScheduler.getServices().updateDailyTaskStatus(profile, tpTask, nextReward.minusSeconds(5));
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (TesseractException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}
			}

		} else {
			emuManager.tapBackButton(EMULATOR_NUMBER);
		}
	}

	public static LocalDateTime parseNextReward(String ocrTime) {
		LocalDateTime now = LocalDateTime.now();

		if (ocrTime == null || ocrTime.isEmpty()) {
			return now;
		}

		// Corrección de errores OCR comunes
		String correctedTime = ocrTime.replaceAll("[Oo]", "0").replaceAll("[lI]", "1").replaceAll("S", "5").replaceAll("[^0-9:]", "");

		try {
			LocalTime parsedTime = LocalTime.parse(correctedTime, DateTimeFormatter.ofPattern("HH:mm:ss"));
			return now.plusHours(parsedTime.getHour()).plusMinutes(parsedTime.getMinute()).plusSeconds(parsedTime.getSecond());
		} catch (DateTimeParseException e) {
			System.err.println("Error al parsear la hora: " + correctedTime);
			return now;
		}
	}

}
