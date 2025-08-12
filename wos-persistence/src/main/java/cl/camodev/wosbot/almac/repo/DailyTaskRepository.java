package cl.camodev.wosbot.almac.repo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import cl.camodev.wosbot.almac.entity.DailyTask;
import cl.camodev.wosbot.almac.entity.TpDailyTask;
import cl.camodev.wosbot.console.enumerable.TpDailyTaskEnum;
import cl.camodev.wosbot.ot.DTODailyTaskStatus;

import cl.camodev.wosbot.almac.jpa.BotPersistence;

public class DailyTaskRepository implements IDailyTaskRepository {
        private static DailyTaskRepository instance;

        private DailyTaskRepository() {
        }

        public static DailyTaskRepository getRepository() {
                if (instance == null) {
                        instance = new DailyTaskRepository();
                }
                return instance;
        }

        private BotPersistence getPersistence(Long profileId) {
                return BotPersistence.getInstance(String.valueOf(profileId));
        }

	@Override
	public boolean addDailyTask(DailyTask dailyTask) {
                Long profileId = dailyTask.getProfile().getId();
                return getPersistence(profileId).createEntity(dailyTask);
	}

	@Override
	public boolean saveDailyTask(DailyTask dailyTask) {
                Long profileId = dailyTask.getProfile().getId();
                return getPersistence(profileId).updateEntity(dailyTask);
	}

	@Override
	public boolean deleteDailyTask(DailyTask dailyTask) {
                Long profileId = dailyTask.getProfile().getId();
                return getPersistence(profileId).deleteEntity(dailyTask);
	}

	@Override
	public DailyTask getDailyTaskById(Long id) {
                // Profile-specific lookup requires profileId; default to global instance
                return BotPersistence.getInstance().findEntityById(DailyTask.class, id);
	}

	@Override
	public List<DailyTask> findByProfileId(Long profileId) {
		String query = "SELECT d FROM DailyTask d WHERE d.profile.id = :profileId";

		// Crear el mapa de parámetros
		Map<String, Object> parameters = new HashMap<>();
		parameters.put("profileId", profileId);

                return getPersistence(profileId).getQueryResults(query, DailyTask.class, parameters);
	}

	@Override
	public DailyTask findByProfileIdAndTaskName(Long profileId, TpDailyTaskEnum taskName) {
		String query = """
				SELECT d FROM DailyTask d
				WHERE d.profile.id = :profileId AND d.task.id = :id""";

		// Crear el mapa de parámetros
		Map<String, Object> parameters = new HashMap<>();
		parameters.put("profileId", profileId);
		parameters.put("id", taskName.getId());

                List<DailyTask> results = getPersistence(profileId).getQueryResults(query, DailyTask.class, parameters);

		return results.isEmpty() ? null : results.get(0);
	}

	@Override
	public Map<Integer, DTODailyTaskStatus> findDailyTasksStatusByProfile(Long profileId) {
		String query = """
				SELECT new cl.camodev.wosbot.ot.DTODailyTaskStatus(
				d.profile.id, d.task.id, d.lastExecution, d.nextSchedule)
				FROM DailyTask d
				WHERE d.profile.id = :profileId""";

		// Crear el mapa de parámetros
		Map<String, Object> parameters = new HashMap<>();
		parameters.put("profileId", profileId);

                List<DTODailyTaskStatus> results = getPersistence(profileId).getQueryResults(query, DTODailyTaskStatus.class, parameters);

		return results.stream().collect(Collectors.toMap(DTODailyTaskStatus::getIdTpDailyTask, dto -> dto));
	}

	@Override
	public TpDailyTask findTpDailyTaskById(Integer id) {
                return BotPersistence.getInstance().findEntityById(TpDailyTask.class, id);
}
}
