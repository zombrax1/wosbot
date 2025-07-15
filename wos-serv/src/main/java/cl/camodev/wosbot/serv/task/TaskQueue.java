package cl.camodev.wosbot.serv.task;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Optional;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.TimeUnit;

import cl.camodev.utiles.UtilTime;
import cl.camodev.wosbot.console.enumerable.EnumConfigurationKey;
import cl.camodev.wosbot.console.enumerable.EnumTpMessageSeverity;
import cl.camodev.wosbot.console.enumerable.TpDailyTaskEnum;
import cl.camodev.wosbot.emulator.EmulatorManager;
import cl.camodev.wosbot.ex.HomeNotFoundException;
import cl.camodev.wosbot.ot.DTOProfileStatus;
import cl.camodev.wosbot.ot.DTOProfiles;
import cl.camodev.wosbot.ot.DTOTaskState;
import cl.camodev.wosbot.serv.impl.ServLogs;
import cl.camodev.wosbot.serv.impl.ServProfiles;
import cl.camodev.wosbot.serv.impl.ServScheduler;
import cl.camodev.wosbot.serv.impl.ServTaskManager;
import cl.camodev.wosbot.serv.task.impl.DailyMissionTask;
import cl.camodev.wosbot.serv.task.impl.InitializeTask;

public class TaskQueue {


	private final PriorityBlockingQueue<DelayedTask> taskQueue = new PriorityBlockingQueue<>();

	// Bandera para detener el loop del scheduler.
	private volatile boolean running = false;

	// Bandera para pausar/reanudar el scheduler.
	private volatile boolean paused = false;

	// Hilo que se encargará de evaluar y ejecutar las tareas.
	private Thread schedulerThread;

	private DTOProfiles profile;

	public TaskQueue(DTOProfiles profile) {
		this.profile = profile;
	}

	/**
	 * Agrega una tarea a la cola.
	 */
	public void addTask(DelayedTask task) {
		taskQueue.offer(task);
	}

	/**
	 * Inicia el procesamiento de la cola.
	 */
	public void start() {

		if (running)
			return;
		running = true;

		schedulerThread = new Thread(() -> {

			boolean idlingTimeExceded = false;
			ServProfiles.getServices().notifyProfileStatusChange(new DTOProfileStatus(profile.getId(), "Getting queue slot"));
			try {
				EmulatorManager.getInstance().adquireEmulatorSlot(profile.getId(), (thread, position) -> {
					ServProfiles.getServices().notifyProfileStatusChange(new DTOProfileStatus(profile.getId(), "Waiting for slot, position: " + position));
				});
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			while (running) {
				// Check if paused and skip execution if so
				if (paused) {
					try {
						ServProfiles.getServices().notifyProfileStatusChange(new DTOProfileStatus(profile.getId(), "PAUSED"));
						Thread.sleep(1000); // Wait 1 second while paused
						continue;
					} catch (InterruptedException e) {
						Thread.currentThread().interrupt();
						break;
					}
				}

				boolean executedTask = false;
				long minDelay = Long.MAX_VALUE;

				// realizar preverificacion de que el jeugo esta coriendo

				Iterator<DelayedTask> it = taskQueue.iterator();
				while (it.hasNext()) {
					DTOTaskState taskState = null;
					DelayedTask task = it.next();
					long delayInSeconds = task.getDelay(TimeUnit.SECONDS);

					// Obtener el menor delay de todas las tareas en la cola
					if (delayInSeconds < minDelay) {
						minDelay = delayInSeconds;
					}

					// Si la tarea está lista para ejecutarse (delay <= 0)
					if (delayInSeconds <= 0) {
						it.remove();

						try {
							ServLogs.getServices().appendLog(EnumTpMessageSeverity.INFO, task.getTaskName(), profile.getName(), "Starting task execution");
							ServProfiles.getServices().notifyProfileStatusChange(new DTOProfileStatus(profile.getId(), "Executing " + task.getTaskName()));

							taskState = new DTOTaskState();
							taskState.setProfileId(profile.getId());
							taskState.setTaskId(task.getTpDailyTaskId());
							taskState.setScheduled(true);
							taskState.setExecuting(true);
							taskState.setLastExecutionTime(LocalDateTime.now());
							taskState.setNextExecutionTime(task.getScheduled());
							ServTaskManager.getInstance().setTaskState(profile.getId(), taskState);

							task.run();
						} catch (HomeNotFoundException e) {
							ServLogs.getServices().appendLog(EnumTpMessageSeverity.ERROR, task.getTaskName(), profile.getName(), e.getMessage());
							addTask(new InitializeTask(profile, TpDailyTaskEnum.INITIALIZE));
						} catch (Exception e) {
							ServLogs.getServices().appendLog(EnumTpMessageSeverity.ERROR, task.getTaskName(), profile.getName(), e.getMessage());
						}

						if (task.isRecurring()) {
							ServLogs.getServices().appendLog(EnumTpMessageSeverity.INFO, task.getTaskName(), profile.getName(), "Next schedule: " + UtilTime.localDateTimeToDDHHMMSS(task.getScheduled()));
							addTask(task);
						} else {
							ServLogs.getServices().appendLog(EnumTpMessageSeverity.INFO, task.getTaskName(), profile.getName(), "Task removed from schedule");
						}

						boolean dailyAutoSchedule = profile.getConfig(EnumConfigurationKey.DAILY_MISSION_AUTO_SCHEDULE_BOOL,Boolean.class);
						if (dailyAutoSchedule) {
							DTOTaskState state = ServTaskManager.getInstance().getTaskState(profile.getId(), TpDailyTaskEnum.DAILY_MISSIONS.getId());
							LocalDateTime next = (state != null)? state.getNextExecutionTime(): null;
							LocalDateTime now = LocalDateTime.now();
							if (task.provideDailyMissionProgress()	&& (state == null || next == null || next.isAfter(now))) {
								DelayedTask prototype = DelayedTaskRegistry.create(TpDailyTaskEnum.DAILY_MISSIONS, profile);

								// verify if the task already exists in the queue
								DelayedTask existing = taskQueue.stream().filter(prototype::equals).findFirst().orElse(null);

								if (existing != null) {
									// task already exists, reschedule it to run now
									taskQueue.remove(existing);
									existing.reschedule(LocalDateTime.now());
									existing.setRecurring(true);
									taskQueue.offer(existing);

									ServLogs.getServices().appendLog(EnumTpMessageSeverity.INFO, "TaskQueue", profile.getName(), "Rescheduled existing " + TpDailyTaskEnum.DAILY_MISSIONS + " to run now");
								} else {
									// task does not exist, create a new instance and schedule it just once
									prototype.reschedule(LocalDateTime.now());
									prototype.setRecurring(false);
									taskQueue.offer(prototype);
									ServLogs.getServices().appendLog(EnumTpMessageSeverity.INFO, "TaskQueue", profile.getName(), "Enqueued new immediate " + TpDailyTaskEnum.DAILY_MISSIONS);
								}



							}
						}


						if (task.provideTriumphProgress()){

						}

						taskState.setExecuting(false);
						taskState.setScheduled(task.isRecurring());
						taskState.setLastExecutionTime(LocalDateTime.now());
						taskState.setNextExecutionTime(task.getScheduled());

						ServTaskManager.getInstance().setTaskState(profile.getId(), taskState);
						ServScheduler.getServices().updateDailyTaskStatus(profile, task.getTpTask(), task.getScheduled());

						executedTask = true;
						break;
					}
				}

				// Verificar condiciones según el delay mínimo de la cola de tareas
				if (minDelay != Long.MAX_VALUE) { // Asegurar que hay tareas en la cola
					long maxIdle = 0;
//					if (profile.getId().equals(Long.valueOf(3L))) {
//						maxIdle = 120L;
//					} else {
					maxIdle = Optional.ofNullable(profile.getGlobalsettings().get(EnumConfigurationKey.MAX_IDLE_TIME_INT.name())).map(Integer::parseInt).orElse(Integer.parseInt(EnumConfigurationKey.MAX_IDLE_TIME_INT.getDefaultValue()));
//					}


					if (!idlingTimeExceded && minDelay > TimeUnit.MINUTES.toSeconds(maxIdle)) {
						idlingTimeExceded = true;
						idlingEmulator(minDelay);
					}

					// Si la demora baja a menos de 1 minuto y intentamos obtener el slot de emulador y encolamos tarea de inicialización
					if (idlingTimeExceded && minDelay < TimeUnit.MINUTES.toSeconds(1)) {
						encolarNuevaTarea();
						idlingTimeExceded = false; // Restablecer la condición para futuras evaluaciones
					}
				}

				// Si no se ejecutó ninguna tarea, esperar un poco antes de volver a evaluar
				if (!executedTask) {
					try {
						String formattedTime;
						if (minDelay == Long.MAX_VALUE || minDelay > 86399) {
							// Si no hay tareas o el delay es muy largo, mostrar un mensaje apropiado
							formattedTime = "No tasks";
						} else {
							DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");
							// Convertir minDelay (segundos) a formato HH:mm:ss
							formattedTime = LocalTime.ofSecondOfDay(minDelay).format(timeFormatter);
						}

						ServProfiles.getServices().notifyProfileStatusChange(new DTOProfileStatus(profile.getId(), "Idling for " + formattedTime + "\nNext task: " + (taskQueue.isEmpty() ? "None" : taskQueue.peek().getTaskName())));
						Thread.sleep(999);
					} catch (InterruptedException e) {
						Thread.currentThread().interrupt();
						break;
					}
				}
			}
		});
		schedulerThread.start();
	}

	// Métodos auxiliares
	private void idlingEmulator(long minDelay) {
		EmulatorManager.getInstance().closeEmulator(profile.getEmulatorNumber());
		ServLogs.getServices().appendLog(EnumTpMessageSeverity.INFO, "TaskQueue", profile.getName(), "Closing game due to large inactivity");
		LocalDateTime scheduledTime = LocalDateTime.now().plusSeconds(minDelay);
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
		ServProfiles.getServices().notifyProfileStatusChange(new DTOProfileStatus(profile.getId(), "Idling till " + formatter.format(scheduledTime)));
		EmulatorManager.getInstance().releaseEmulatorSlot();
	}

	private void encolarNuevaTarea() {
		ServLogs.getServices().appendLog(EnumTpMessageSeverity.INFO, "TaskQueue", profile.getName(), "shcheduled task's will start soon");

		try {
			EmulatorManager.getInstance().adquireEmulatorSlot(profile.getId(), (thread, position) -> {
				ServProfiles.getServices().notifyProfileStatusChange(new DTOProfileStatus(profile.getId(), "Waiting for slot, position: " + position));
			});
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		addTask(new InitializeTask(profile, TpDailyTaskEnum.INITIALIZE));
	}

	/**
	 * Detiene inmediatamente el procesamiento de la cola, sin importar en qué estado esté.
	 */
	public void stop() {
		running = false; // Detener el bucle principal

		if (schedulerThread != null) {
			schedulerThread.interrupt(); // Interrumpir el hilo para forzar la salida inmediata

			try {
				schedulerThread.join(1000); // Esperar hasta 1 segundo para que el hilo termine
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
		}

		// Eliminar todas las tareas pendientes en la cola
		taskQueue.clear();
		ServProfiles.getServices().notifyProfileStatusChange(new DTOProfileStatus(profile.getId(), "NOT RUNNING "));
		System.out.println("TaskQueue stopped immediately.");
	}

	/**
	 * Pausa el procesamiento de la cola, manteniendo las tareas en la cola.
	 */
	public void pause() {
		paused = true;
		ServProfiles.getServices().notifyProfileStatusChange(new DTOProfileStatus(profile.getId(), "PAUSE REQUESTED"));
		System.out.println("TaskQueue pausada.");
	}

	/**
	 * Reanuda el procesamiento de la cola.
	 */
	public void resume() {
		paused = false;
		ServProfiles.getServices().notifyProfileStatusChange(new DTOProfileStatus(profile.getId(), "RESUMING"));
		System.out.println("TaskQueue reanudada.");
	}

	public void executeTaskNow(TpDailyTaskEnum taskEnum) {

		// Obtain the task prototype from the registry
		DelayedTask prototype = DelayedTaskRegistry.create(taskEnum, profile);
		if (prototype == null) {
			ServLogs.getServices().appendLog(EnumTpMessageSeverity.WARNING, "TaskQueue", profile.getName(), "Task not found: " + taskEnum);
			return;
		}

		// verify if the task already exists in the queue
		DelayedTask existing = taskQueue.stream().filter(prototype::equals).findFirst().orElse(null);

		if (existing != null) {
			// task already exists, reschedule it to run now
			taskQueue.remove(existing);
			existing.reschedule(LocalDateTime.now());
			existing.setRecurring(true);
			taskQueue.offer(existing);

			ServLogs.getServices().appendLog(EnumTpMessageSeverity.INFO, "TaskQueue", profile.getName(), "Rescheduled existing " + taskEnum + " to run now");
		} else {
			// task does not exist, create a new instance and schedule it just once
			prototype.reschedule(LocalDateTime.now());
			prototype.setRecurring(false);
			taskQueue.offer(prototype);
			ServLogs.getServices().appendLog(EnumTpMessageSeverity.INFO, "TaskQueue", profile.getName(), "Enqueued new immediate " + taskEnum);
		}

		DTOTaskState taskState = new DTOTaskState();
		taskState.setProfileId(profile.getId());
		taskState.setTaskId(taskEnum.getId());
		taskState.setScheduled(true);
		taskState.setExecuting(false);
		taskState.setLastExecutionTime(prototype.getScheduled());
		taskState.setNextExecutionTime(LocalDateTime.now());
		ServTaskManager.getInstance().setTaskState(profile.getId(), taskState);
	}

	public boolean isTaskScheduled(TpDailyTaskEnum taskEnum) {
		// Obtain the task prototype from the registry
		DelayedTask prototype = DelayedTaskRegistry.create(taskEnum, profile);
		if (prototype == null) {
			ServLogs.getServices().appendLog(EnumTpMessageSeverity.WARNING, "TaskQueue", profile.getName(), "Task not found: " + taskEnum);
			return false;
		}
		// Check if the task is enabled in the queue

		return taskQueue.stream().anyMatch(task -> task.equals(prototype));
	}

}
