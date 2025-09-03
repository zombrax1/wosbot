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

class MailRewardsTaskTest {

    @Test
    void stopsAfterMaxAttempts() {
        DTOProfiles profile = new DTOProfiles(1L);
        profile.setEmulatorNumber("emu");
        profile.setName("p");
        profile.setConfig(EnumConfigurationKey.MAIL_REWARDS_OFFSET_INT, 0);

        AtomicBoolean warned = new AtomicBoolean(false);
        EmulatorManager emu = mock(EmulatorManager.class);
        ServScheduler scheduler = mock(ServScheduler.class);
        MailRewardsTask task = new TestMailRewardsTask(profile, emu, scheduler, warned);

        DTOImageSearchResult found = new DTOImageSearchResult(true, new DTOPoint(0, 0), 100);
        DTOImageSearchResult notFound = new DTOImageSearchResult(false, null, 0);

        when(emu.searchTemplate(anyString(), eq(EnumTemplates.GAME_HOME_FURNACE.getTemplate()), anyDouble()))
                .thenReturn(found);
        when(emu.searchTemplate(anyString(), eq(EnumTemplates.GAME_HOME_WORLD.getTemplate()), anyDouble()))
                .thenReturn(notFound);
        when(emu.searchTemplate(anyString(), eq(EnumTemplates.MAIL_UNCLAIMED_REWARDS.getTemplate()), anyDouble()))
                .thenReturn(found);

        task.execute();

        verify(emu, times(MailRewardsTask.MAX_MAIL_SEARCH_ATTEMPTS * 3))
                .searchTemplate(anyString(), eq(EnumTemplates.MAIL_UNCLAIMED_REWARDS.getTemplate()), anyDouble());
        assertTrue(warned.get());
    }

    private static class TestMailRewardsTask extends MailRewardsTask {
        private final AtomicBoolean warned;

        TestMailRewardsTask(DTOProfiles profile, EmulatorManager emu, ServScheduler scheduler, AtomicBoolean warned) {
            super(profile, TpDailyTaskEnum.MAIL_REWARDS);
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
