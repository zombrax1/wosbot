package cl.camodev.wosbot.serv.task.impl;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

import java.util.concurrent.atomic.AtomicBoolean;

import org.junit.jupiter.api.Test;

import cl.camodev.wosbot.console.enumerable.EnumConfigurationKey;
import cl.camodev.wosbot.console.enumerable.EnumTemplates;
import cl.camodev.wosbot.console.enumerable.TpDailyTaskEnum;
import cl.camodev.wosbot.emulator.EmulatorManager;
import cl.camodev.wosbot.ot.DTOImageSearchResult;
import cl.camodev.wosbot.ot.DTOPoint;
import cl.camodev.wosbot.ot.DTOProfiles;
import cl.camodev.wosbot.serv.impl.ServScheduler;

class AllianceChestTaskTest {

    @Test
    void stopsAfterMaxAttempts() {
        DTOProfiles profile = new DTOProfiles(1L);
        profile.setEmulatorNumber("emu");
        profile.setName("p");
        profile.setConfig(EnumConfigurationKey.ALLIANCE_CHESTS_OFFSET_INT, 0);
        profile.setConfig(EnumConfigurationKey.ALLIANCE_HONOR_CHEST_BOOL, false);

        AtomicBoolean warned = new AtomicBoolean(false);
        EmulatorManager emu = mock(EmulatorManager.class);
        ServScheduler scheduler = mock(ServScheduler.class);
        AllianceChestTask task = new TestAllianceChestTask(profile, emu, scheduler, warned);

        DTOImageSearchResult found = new DTOImageSearchResult(true, new DTOPoint(0, 0), 100);
        DTOImageSearchResult notFound = new DTOImageSearchResult(false, null, 0);

        when(emu.searchTemplate(anyString(), eq(EnumTemplates.ALLIANCE_CHEST_BUTTON.getTemplate()), anyDouble()))
                .thenReturn(found);
        when(emu.searchTemplate(anyString(), eq(EnumTemplates.ALLIANCE_CHEST_CLAIM_ALL_BUTTON.getTemplate()), anyDouble()))
                .thenReturn(notFound);
        when(emu.searchTemplate(anyString(), eq(EnumTemplates.ALLIANCE_CHEST_CLAIM_BUTTON.getTemplate()), anyDouble()))
                .thenReturn(found);

        task.execute();

        verify(emu, times(AllianceChestTask.MAX_CLAIM_ATTEMPTS))
                .searchTemplate(anyString(), eq(EnumTemplates.ALLIANCE_CHEST_CLAIM_BUTTON.getTemplate()), anyDouble());
        assertTrue(warned.get());
    }

    private static class TestAllianceChestTask extends AllianceChestTask {
        private final AtomicBoolean warned;

        TestAllianceChestTask(DTOProfiles profile, EmulatorManager emu, ServScheduler scheduler, AtomicBoolean warned) {
            super(profile, TpDailyTaskEnum.ALLIANCE_CHESTS);
            this.emuManager = emu;
            this.servScheduler = scheduler;
            this.warned = warned;
        }

        @Override
        protected void sleepTask(long millis) {
        }

        @Override
        public void logWarning(String message) {
            warned.set(true);
        }
    }
}
