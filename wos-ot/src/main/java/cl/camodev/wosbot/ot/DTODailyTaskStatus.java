package cl.camodev.wosbot.ot;

import java.time.LocalDateTime;

public class DTODailyTaskStatus {
    private Long id;
    private int idTpDailyTask;
    private LocalDateTime lastExecution;
    private LocalDateTime nextSchedule;

    public DTODailyTaskStatus() {}

    public DTODailyTaskStatus(Long id, int idTpDailyTask, LocalDateTime lastExecution, LocalDateTime nextSchedule) {
        this.id = id;
        this.idTpDailyTask = idTpDailyTask;
        this.lastExecution = lastExecution;
        this.nextSchedule = nextSchedule;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public int getIdTpDailyTask() { return idTpDailyTask; }
    public void setIdTpDailyTask(int idTpDailyTask) { this.idTpDailyTask = idTpDailyTask; }
    public LocalDateTime getLastExecution() { return lastExecution; }
    public void setLastExecution(LocalDateTime lastExecution) { this.lastExecution = lastExecution; }
    public LocalDateTime getNextSchedule() { return nextSchedule; }
    public void setNextSchedule(LocalDateTime nextSchedule) { this.nextSchedule = nextSchedule; }
}
