package cl.camodev.wosbot.almac.jpa;

import cl.camodev.wosbot.almac.entity.TpConfig;
import cl.camodev.wosbot.almac.entity.TpDailyTask;
import cl.camodev.wosbot.console.enumerable.TpConfigEnum;
import cl.camodev.wosbot.console.enumerable.TpDailyTaskEnum;

public class PersistenceDataInitialization {

        private PersistenceDataInitialization() {
        }

        public static void initializeData(BotPersistence persistence) {
                for (TpDailyTaskEnum taskEnum : TpDailyTaskEnum.values()) {
                        TpDailyTask existingTask = persistence.findEntityById(TpDailyTask.class, taskEnum.getId());
                        if (existingTask == null) {
                                persistence.createEntity(new TpDailyTask(taskEnum));
                        }
                }

                for (TpConfigEnum tpConfigEnum : TpConfigEnum.values()) {
                        TpConfig existingTpConfig = persistence.findEntityById(TpConfig.class, tpConfigEnum.getId());
                        if (existingTpConfig == null) {
                                persistence.createEntity(new TpConfig(tpConfigEnum));
                        }
                }
        }
}
