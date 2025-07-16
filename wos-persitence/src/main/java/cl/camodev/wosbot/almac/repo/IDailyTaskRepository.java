package cl.camodev.wosbot.almac.repo;

import java.util.Map;

import cl.camodev.wosbot.almac.entity.DailyTask;
import cl.camodev.wosbot.almac.entity.TpDailyTask;
import cl.camodev.wosbot.console.enumerable.TpDailyTaskEnum;
import cl.camodev.wosbot.ot.DTODailyTaskStatus;

public interface IDailyTaskRepository {
    Map<Integer, DTODailyTaskStatus> findDailyTasksStatusByProfile(Long profileId);
    DailyTask findByProfileIdAndTaskName(Long profileId, TpDailyTaskEnum task);
    TpDailyTask findTpDailyTaskById(int id);
    boolean addDailyTask(DailyTask dailyTask);
    boolean saveDailyTask(DailyTask dailyTask);
}
