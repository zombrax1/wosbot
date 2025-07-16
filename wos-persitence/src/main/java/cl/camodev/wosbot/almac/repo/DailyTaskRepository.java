package cl.camodev.wosbot.almac.repo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import cl.camodev.wosbot.almac.entity.DailyTask;
import cl.camodev.wosbot.almac.entity.TpDailyTask;
import cl.camodev.wosbot.almac.jpa.BotPersistence;
import cl.camodev.wosbot.console.enumerable.TpDailyTaskEnum;
import cl.camodev.wosbot.ot.DTODailyTaskStatus;

public class DailyTaskRepository implements IDailyTaskRepository {
    private static DailyTaskRepository instance;
    private final BotPersistence persistence = BotPersistence.getInstance();

    private DailyTaskRepository() {}

    public static DailyTaskRepository getRepository() {
        if (instance == null) {
            instance = new DailyTaskRepository();
        }
        return instance;
    }

    @Override
    public Map<Integer, DTODailyTaskStatus> findDailyTasksStatusByProfile(Long profileId) {
        String q = "SELECT new cl.camodev.wosbot.ot.DTODailyTaskStatus(d.id, d.task.id, d.lastExecution, d.nextSchedule) " +
                "FROM DailyTask d WHERE d.profile.id = :profileId";
        Map<String, Object> p = new HashMap<>();
        p.put("profileId", profileId);
        List<DTODailyTaskStatus> list = persistence.getQueryResults(q, DTODailyTaskStatus.class, p);
        return list.stream().collect(Collectors.toMap(DTODailyTaskStatus::getIdTpDailyTask, e -> e));
    }

    @Override
    public DailyTask findByProfileIdAndTaskName(Long profileId, TpDailyTaskEnum task) {
        String q = "SELECT d FROM DailyTask d WHERE d.profile.id = :profileId AND d.task.id = :taskId";
        Map<String, Object> p = new HashMap<>();
        p.put("profileId", profileId);
        p.put("taskId", task.getId());
        List<DailyTask> list = persistence.getQueryResults(q, DailyTask.class, p);
        return list.isEmpty() ? null : list.get(0);
    }

    @Override
    public TpDailyTask findTpDailyTaskById(int id) {
        return persistence.findEntityById(TpDailyTask.class, id);
    }

    @Override
    public boolean addDailyTask(DailyTask dailyTask) {
        return persistence.createEntity(dailyTask);
    }

    @Override
    public boolean saveDailyTask(DailyTask dailyTask) {
        return persistence.updateEntity(dailyTask);
    }
}
