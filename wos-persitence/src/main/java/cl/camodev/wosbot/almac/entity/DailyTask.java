package cl.camodev.wosbot.almac.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "daily_task")
public class DailyTask {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Profile profile;

    @ManyToOne
    private TpDailyTask task;

    private LocalDateTime lastExecution;
    private LocalDateTime nextSchedule;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Profile getProfile() { return profile; }
    public void setProfile(Profile profile) { this.profile = profile; }
    public TpDailyTask getTask() { return task; }
    public void setTask(TpDailyTask task) { this.task = task; }
    public LocalDateTime getLastExecution() { return lastExecution; }
    public void setLastExecution(LocalDateTime lastExecution) { this.lastExecution = lastExecution; }
    public LocalDateTime getNextSchedule() { return nextSchedule; }
    public void setNextSchedule(LocalDateTime nextSchedule) { this.nextSchedule = nextSchedule; }
}
