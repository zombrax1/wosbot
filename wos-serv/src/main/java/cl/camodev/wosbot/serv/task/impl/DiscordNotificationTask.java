package cl.camodev.wosbot.serv.task.impl;

import java.io.IOException;
import java.time.LocalDateTime;

import cl.camodev.wosbot.console.enumerable.EnumConfigurationKey;
import cl.camodev.wosbot.console.enumerable.EnumTemplates;
import cl.camodev.wosbot.console.enumerable.EnumTpMessageSeverity;
import cl.camodev.wosbot.console.enumerable.TpDailyTaskEnum;
import cl.camodev.wosbot.emulator.EmulatorManager;
import cl.camodev.wosbot.ot.DTOImageSearchResult;
import cl.camodev.wosbot.ot.DTOPoint;
import cl.camodev.wosbot.ot.DTOProfiles;
import cl.camodev.wosbot.serv.impl.DiscordService;
import cl.camodev.wosbot.serv.impl.ServLogs;
import cl.camodev.wosbot.serv.task.DelayedTask;
import net.sourceforge.tess4j.TesseractException;

public class DiscordNotificationTask extends DelayedTask {

    public DiscordNotificationTask(DTOProfiles profile, TpDailyTaskEnum tpDailyTask) {
        super(profile, tpDailyTask);
    }

    @Override
    protected void execute() {
        // Check if Discord is enabled for this profile
        boolean discordEnabled = profile.getConfig(EnumConfigurationKey.DISCORD_ENABLED_BOOL, Boolean.class);
        if (!discordEnabled) {
            logInfo("Discord notifications disabled for this profile");
            rescheduleTask();
            return;
        }

        // Initialize Discord service
        String token = profile.getConfig(EnumConfigurationKey.DISCORD_TOKEN_STRING, String.class);
        String channelId = profile.getConfig(EnumConfigurationKey.DISCORD_CHANNEL_ID_STRING, String.class);
        
        DiscordService discordService = DiscordService.getInstance();
        if (!discordService.isConnected()) {
            if (!discordService.initialize(token, channelId)) {
                logError("Failed to initialize Discord service");
                rescheduleTask();
                return;
            }
        }

        logInfo("Checking for alliance activities to notify Discord");

        // Step 1: Find and click alliance.png (alliance icon)
        logInfo("Searching for alliance button with 50% threshold...");
        DTOImageSearchResult allianceResult = emuManager.searchTemplate(EMULATOR_NUMBER,
                "/templates/discord/alliance.png", 0, 0, 720, 1280, 50);
        
        if (allianceResult.isFound()) {
            logInfo("✅ Alliance button found at: " + allianceResult.getPoint().getX() + ", " + allianceResult.getPoint().getY() + " (match: " + allianceResult.getMatchPercentage() + "%)");
            emuManager.tapAtRandomPoint(EMULATOR_NUMBER, allianceResult.getPoint(), allianceResult.getPoint());
            sleepTask(3000);
            
            // Step 2: Find and click notice.png (notice button)
            logInfo("Searching for notice button with 50% threshold...");
            DTOImageSearchResult noticeResult = emuManager.searchTemplate(EMULATOR_NUMBER,
                    "/templates/discord/notice.png", 0, 0, 720, 1280, 50);
            
            if (noticeResult.isFound()) {
                logInfo("✅ Notice button found at: " + noticeResult.getPoint().getX() + ", " + noticeResult.getPoint().getY() + " (match: " + noticeResult.getMatchPercentage() + "%)");
                emuManager.tapAtRandomPoint(EMULATOR_NUMBER, noticeResult.getPoint(), noticeResult.getPoint());
                sleepTask(2000);
                
                // Step 3: Read text from the notice area using OCR
                logInfo("Reading notice text with OCR...");
                
                // Calculate OCR region around the notice click point - wider area to capture all text
                int x1 = Math.max(0, noticeResult.getPoint().getX() - 300);
                int y1 = Math.max(0, noticeResult.getPoint().getY() - 200);
                int x2 = Math.min(720, noticeResult.getPoint().getX() + 300);
                int y2 = Math.min(1280, noticeResult.getPoint().getY() + 400);
                
                DTOPoint ocrStart = new DTOPoint(x1, y1);
                DTOPoint ocrEnd = new DTOPoint(x2, y2);
                
                logInfo("OCR Region: (" + x1 + "," + y1 + ") to (" + x2 + "," + y2 + ") - Size: " + (x2-x1) + "x" + (y2-y1));
                
                try {
                    String noticeText = emuManager.ocrRegionText(EMULATOR_NUMBER, ocrStart, ocrEnd);
                    logInfo("OCR Result: " + (noticeText != null ? noticeText : "null"));
                    
                    if (noticeText != null && !noticeText.trim().isEmpty()) {
                        String message = "📢 **Alliance Notice:**\n" + noticeText.trim();
                        discordService.sendNotification(message, profile);
                        logInfo("Discord notification sent: Alliance notice detected - " + noticeText.trim());
                    } else {
                        logInfo("No text found in notice area");
                    }
                } catch (IOException | TesseractException e) {
                    logError("Error reading alliance notice text: " + e.getMessage());
                }
                
                // Step 4: Find and click close.png to close notification
                logInfo("Searching for close button with 50% threshold...");
                DTOImageSearchResult closeResult = emuManager.searchTemplate(EMULATOR_NUMBER,
                        "/templates/discord/close.png", 0, 0, 720, 1280, 50);
                
                if (closeResult.isFound()) {
                    logInfo("✅ Close button found at: " + closeResult.getPoint().getX() + ", " + closeResult.getPoint().getY() + " (match: " + closeResult.getMatchPercentage() + "%)");
                    emuManager.tapAtRandomPoint(EMULATOR_NUMBER, closeResult.getPoint(), closeResult.getPoint());
                    sleepTask(500);
                } else {
                    logInfo("❌ Close button not found, using back button");
                    emuManager.tapBackButton(EMULATOR_NUMBER);
                    sleepTask(500);
                }
            } else {
                logInfo("❌ Notice button not found - template may need to be recreated");
            }
        } else {
            logInfo("❌ Alliance button not found - template may need to be recreated");
        }

        // Check for alliance help requests
        if (profile.getConfig(EnumConfigurationKey.DISCORD_HELP_NOTIFICATIONS_BOOL, Boolean.class)) {
            checkAllianceHelpRequests(discordService);
        }

        // Check for alliance chests
        if (profile.getConfig(EnumConfigurationKey.DISCORD_CHEST_NOTIFICATIONS_BOOL, Boolean.class)) {
            checkAllianceChests(discordService);
        }

        // Check for alliance tech contributions
        if (profile.getConfig(EnumConfigurationKey.DISCORD_TECH_NOTIFICATIONS_BOOL, Boolean.class)) {
            checkAllianceTech(discordService);
        }

        // Check for alliance triumph rewards
        if (profile.getConfig(EnumConfigurationKey.DISCORD_TRIUMPH_NOTIFICATIONS_BOOL, Boolean.class)) {
            checkAllianceTriumph(discordService);
        }

        // Go back to home
        emuManager.tapBackButton(EMULATOR_NUMBER);
        sleepTask(1000);

        rescheduleTask();
    }

    private void checkAllianceHelpRequests(DiscordService discordService) {
        try {
            DTOImageSearchResult helpResult = emuManager.searchTemplate(EMULATOR_NUMBER,
                    EnumTemplates.ALLIANCE_HELP_BUTTON.getTemplate(), 0, 0, 720, 1280, 90);
            
            if (helpResult.isFound()) {
                emuManager.tapAtRandomPoint(EMULATOR_NUMBER, helpResult.getPoint(), helpResult.getPoint());
                sleepTask(500);

                DTOImageSearchResult helpRequestsResult = emuManager.searchTemplate(EMULATOR_NUMBER,
                        EnumTemplates.ALLIANCE_HELP_REQUESTS.getTemplate(), 0, 0, 720, 1280, 90);
                
                if (helpRequestsResult.isFound()) {
                    String message = "🆘 **Alliance Help Requests Available!**\nMembers need your help - check the alliance menu!";
                    discordService.sendNotification(message, profile);
                    logInfo("Discord notification sent: Alliance help requests available");
                }
                
                emuManager.tapBackButton(EMULATOR_NUMBER);
                sleepTask(500);
            }
        } catch (Exception e) {
            logError("Error checking alliance help requests: " + e.getMessage());
        }
    }

    private void checkAllianceChests(DiscordService discordService) {
        try {
            DTOImageSearchResult chestResult = emuManager.searchTemplate(EMULATOR_NUMBER,
                    EnumTemplates.ALLIANCE_CHEST_BUTTON.getTemplate(), 0, 0, 720, 1280, 90);
            
            if (chestResult.isFound()) {
                emuManager.tapAtRandomPoint(EMULATOR_NUMBER, chestResult.getPoint(), chestResult.getPoint());
                sleepTask(500);

                // Check if chest is ready to claim
                DTOImageSearchResult claimButton = emuManager.searchTemplate(EMULATOR_NUMBER,
                        EnumTemplates.ALLIANCE_CHEST_LOOT_CLAIM_BUTTON.getTemplate(), 0, 0, 720, 1280, 90);
                
                if (claimButton.isFound()) {
                    String message = "📦 **Alliance Chest Ready!**\nAlliance chest rewards are available to claim!";
                    discordService.sendNotification(message, profile);
                    logInfo("Discord notification sent: Alliance chest ready");
                }
                
                emuManager.tapBackButton(EMULATOR_NUMBER);
                sleepTask(500);
            }
        } catch (Exception e) {
            logError("Error checking alliance chests: " + e.getMessage());
        }
    }

    private void checkAllianceTech(DiscordService discordService) {
        try {
            DTOImageSearchResult techResult = emuManager.searchTemplate(EMULATOR_NUMBER,
                    EnumTemplates.ALLIANCE_TECH_BUTTON.getTemplate(), 0, 0, 720, 1280, 90);
            
            if (techResult.isFound()) {
                emuManager.tapAtRandomPoint(EMULATOR_NUMBER, techResult.getPoint(), techResult.getPoint());
                sleepTask(500);

                // Check for thumb up button (tech contribution available)
                DTOImageSearchResult thumbUpResult = emuManager.searchTemplate(EMULATOR_NUMBER,
                        EnumTemplates.ALLIANCE_TECH_THUMB_UP.getTemplate(), 0, 0, 720, 1280, 90);
                
                if (thumbUpResult.isFound()) {
                    String message = "🔬 **Alliance Tech Contribution Available!**\nYou can contribute to alliance technology research!";
                    discordService.sendNotification(message, profile);
                    logInfo("Discord notification sent: Alliance tech contribution available");
                }
                
                emuManager.tapBackButton(EMULATOR_NUMBER);
                sleepTask(500);
            }
        } catch (Exception e) {
            logError("Error checking alliance tech: " + e.getMessage());
        }
    }

    private void checkAllianceTriumph(DiscordService discordService) {
        try {
            DTOImageSearchResult triumphResult = emuManager.searchTemplate(EMULATOR_NUMBER,
                    EnumTemplates.ALLIANCE_TRIUMPH_BUTTON.getTemplate(), 0, 0, 720, 1280, 90);
            
            if (triumphResult.isFound()) {
                emuManager.tapAtRandomPoint(EMULATOR_NUMBER, triumphResult.getPoint(), triumphResult.getPoint());
                sleepTask(2000);

                // Check for daily triumph rewards
                DTOImageSearchResult dailyTriumph = emuManager.searchTemplate(EMULATOR_NUMBER,
                        EnumTemplates.ALLIANCE_TRIUMPH_DAILY.getTemplate(), 0, 0, 720, 1280, 90);
                
                if (dailyTriumph.isFound()) {
                    String message = "🏆 **Daily Triumph Rewards Ready!**\nDaily alliance triumph rewards are available to claim!";
                    discordService.sendNotification(message, profile);
                    logInfo("Discord notification sent: Daily triumph rewards ready");
                }

                // Check for weekly triumph rewards
                DTOImageSearchResult weeklyTriumph = emuManager.searchTemplate(EMULATOR_NUMBER,
                        EnumTemplates.ALLIANCE_TRIUMPH_WEEKLY.getTemplate(), 0, 0, 720, 1280, 90);
                
                if (weeklyTriumph.isFound()) {
                    String message = "🏆 **Weekly Triumph Rewards Ready!**\nWeekly alliance triumph rewards are available to claim!";
                    discordService.sendNotification(message, profile);
                    logInfo("Discord notification sent: Weekly triumph rewards ready");
                }
                
                emuManager.tapBackButton(EMULATOR_NUMBER);
                sleepTask(500);
            }
        } catch (Exception e) {
            logError("Error checking alliance triumph: " + e.getMessage());
        }
    }

    private void checkAllianceNotices(DiscordService discordService) {
        try {
            // Use coordinates from macro for alliance notice area
            // Macro coordinates from user's log:
            // Second touch: (10020, 4290) - normalized to 720x1280: (477, 1000)
            // This appears to be for alliance notices/help area
            DTOPoint noticeStart = new DTOPoint(400, 900); // Alliance notice area start
            DTOPoint noticeEnd = new DTOPoint(550, 1100);  // Alliance notice area end
            
            String noticeText = emuManager.ocrRegionText(EMULATOR_NUMBER, noticeStart, noticeEnd);
            
            if (noticeText != null && !noticeText.trim().isEmpty()) {
                // Process the notice text and send Discord notification
                String message = "📢 **Alliance Notice:**\n" + noticeText.trim();
                discordService.sendNotification(message, profile);
                logInfo("Discord notification sent: Alliance notice detected - " + noticeText.trim());
            }
        } catch (IOException | TesseractException e) {
            logError("Error reading alliance notices: " + e.getMessage());
        } catch (Exception e) {
            logError("Error checking alliance notices: " + e.getMessage());
        }
    }

    private void rescheduleTask() {
        // Get scroll interval from profile configuration, default to 30 minutes
        int scrollInterval = profile.getConfig(EnumConfigurationKey.DISCORD_SCROLL_INTERVAL_INT, Integer.class);
        LocalDateTime nextExecutionTime = LocalDateTime.now().plusMinutes(scrollInterval);
        this.reschedule(nextExecutionTime);
    }
} 