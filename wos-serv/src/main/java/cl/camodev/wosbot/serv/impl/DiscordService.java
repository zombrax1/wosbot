package cl.camodev.wosbot.serv.impl;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import cl.camodev.wosbot.console.enumerable.EnumTpMessageSeverity;
import cl.camodev.wosbot.ot.DTOProfiles;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;

public class DiscordService {
    private static DiscordService instance;
    private JDA jda;
    private boolean isConnected = false;
    private String channelId;
    private String botToken;

    private DiscordService() {
    }

    public static DiscordService getInstance() {
        if (instance == null) {
            instance = new DiscordService();
        }
        return instance;
    }

    public boolean initialize(String token, String channelId) {
        if (token == null || token.trim().isEmpty() || channelId == null || channelId.trim().isEmpty()) {
            ServLogs.getServices().appendLog(EnumTpMessageSeverity.ERROR, "DiscordService", "-", "Invalid Discord configuration: token or channel ID is empty");
            return false;
        }

        this.botToken = token;
        this.channelId = channelId;

        try {
            jda = JDABuilder.createDefault(token)
                    .setChunkingFilter(ChunkingFilter.ALL)
                    .setMemberCachePolicy(MemberCachePolicy.ALL)
                    .enableIntents(GatewayIntent.GUILD_MESSAGES, GatewayIntent.MESSAGE_CONTENT, GatewayIntent.GUILD_MEMBERS)
                    .build();

            jda.awaitReady();
            isConnected = true;
            
            ServLogs.getServices().appendLog(EnumTpMessageSeverity.INFO, "DiscordService", "-", "Discord bot connected successfully");
            return true;
        } catch (Exception e) {
            ServLogs.getServices().appendLog(EnumTpMessageSeverity.ERROR, "DiscordService", "-", "Failed to connect to Discord: " + e.getMessage());
            isConnected = false;
            return false;
        }
    }

    public void sendNotification(String message, DTOProfiles profile) {
        if (!isConnected || jda == null) {
            ServLogs.getServices().appendLog(EnumTpMessageSeverity.WARNING, "DiscordService", profile.getName(), "Discord not connected, cannot send notification");
            return;
        }

        try {
            TextChannel channel = jda.getTextChannelById(channelId);
            if (channel == null) {
                ServLogs.getServices().appendLog(EnumTpMessageSeverity.ERROR, "DiscordService", profile.getName(), "Discord channel not found: " + channelId);
                return;
            }

            String formattedMessage = formatMessage(message, profile);
            channel.sendMessage(formattedMessage).queue(
                success -> ServLogs.getServices().appendLog(EnumTpMessageSeverity.INFO, "DiscordService", profile.getName(), "Notification sent successfully"),
                error -> ServLogs.getServices().appendLog(EnumTpMessageSeverity.ERROR, "DiscordService", profile.getName(), "Failed to send notification: " + error.getMessage())
            );
        } catch (Exception e) {
            ServLogs.getServices().appendLog(EnumTpMessageSeverity.ERROR, "DiscordService", profile.getName(), "Error sending Discord notification: " + e.getMessage());
        }
    }

    private String formatMessage(String message, DTOProfiles profile) {
        LocalDateTime now = LocalDateTime.now();
        String timestamp = now.format(DateTimeFormatter.ofPattern("HH:mm:ss"));
        
        return String.format("**🤖 WOS Bot Notification**\n" +
                           "**Account:** %s\n" +
                           "**Time:** %s\n" +
                           "**Message:** %s", 
                           profile.getName(), timestamp, message);
    }

    public void disconnect() {
        if (jda != null) {
            jda.shutdown();
            isConnected = false;
            ServLogs.getServices().appendLog(EnumTpMessageSeverity.INFO, "DiscordService", "-", "Discord bot disconnected");
        }
    }

    public boolean isConnected() {
        return isConnected;
    }

    public String getChannelId() {
        return channelId;
    }

    public String getBotToken() {
        return botToken;
    }

    public boolean testConnection(String token, String channelId) {
        if (token == null || token.trim().isEmpty() || channelId == null || channelId.trim().isEmpty()) {
            return false;
        }

        try {
            JDA testJda = JDABuilder.createDefault(token)
                    .setChunkingFilter(ChunkingFilter.ALL)
                    .setMemberCachePolicy(MemberCachePolicy.ALL)
                    .enableIntents(GatewayIntent.GUILD_MESSAGES, GatewayIntent.MESSAGE_CONTENT, GatewayIntent.GUILD_MEMBERS)
                    .build();

            testJda.awaitReady();
            
            // Test if we can access the channel
            TextChannel testChannel = testJda.getTextChannelById(channelId);
            if (testChannel == null) {
                testJda.shutdown();
                return false;
            }

            testJda.shutdown();
            return true;
        } catch (Exception e) {
            return false;
        }
    }
} 