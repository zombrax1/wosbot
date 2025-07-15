package cl.camodev.wosbot.discord.view;

import cl.camodev.wosbot.common.view.AbstractProfileController;
import cl.camodev.wosbot.console.enumerable.EnumConfigurationKey;
import cl.camodev.wosbot.console.enumerable.EnumTpMessageSeverity;
import cl.camodev.wosbot.ot.DTOProfiles;
import cl.camodev.wosbot.profile.model.ProfileAux;
import cl.camodev.wosbot.serv.impl.DiscordService;
import cl.camodev.wosbot.serv.impl.ServLogs;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

import java.util.concurrent.CompletableFuture;

public class DiscordLayoutController extends AbstractProfileController {

    @FXML
    private CheckBox checkBoxDiscordEnabled;

    @FXML
    private TextField textfieldDiscordToken, textfieldDiscordChannelId, textfieldScrollInterval;

    @FXML
    private Button buttonTestConnection, buttonSendTestMessage, buttonSaveSettings;

    @FXML
    private Label labelConnectionStatus;

    @FXML
    private Circle circleConnectionStatus;

    @FXML
    private VBox vboxNotificationSettings;

    @FXML
    private void initialize() {
        // Map checkboxes to configuration keys
        checkBoxMappings.put(checkBoxDiscordEnabled, EnumConfigurationKey.DISCORD_ENABLED_BOOL);

        // Map text fields to configuration keys
        textFieldMappings.put(textfieldDiscordToken, EnumConfigurationKey.DISCORD_TOKEN_STRING);
        textFieldMappings.put(textfieldDiscordChannelId, EnumConfigurationKey.DISCORD_CHANNEL_ID_STRING);
        textFieldMappings.put(textfieldScrollInterval, EnumConfigurationKey.DISCORD_SCROLL_INTERVAL_INT);

        initializeChangeEvents();
        setupConnectionStatus();
        setupTestButtons();
    }

    private void setupConnectionStatus() {
        updateConnectionStatus(false);
    }

    private void setupTestButtons() {
        buttonTestConnection.setOnAction(e -> testDiscordConnection());
        buttonSendTestMessage.setOnAction(e -> sendTestMessage());
        buttonSaveSettings.setOnAction(e -> saveSettings());
    }

    private void testDiscordConnection() {
        String token = textfieldDiscordToken.getText();
        String channelId = textfieldDiscordChannelId.getText();

        if (token == null || token.trim().isEmpty() || channelId == null || channelId.trim().isEmpty()) {
            showAlert("Error", "Please enter both Discord Bot Token and Channel ID");
            return;
        }

        buttonTestConnection.setDisable(true);
        buttonTestConnection.setText("Testing...");

        CompletableFuture.runAsync(() -> {
            DiscordService discordService = DiscordService.getInstance();
            boolean success = discordService.testConnection(token, channelId);

            Platform.runLater(() -> {
                updateConnectionStatus(success);
                buttonTestConnection.setDisable(false);
                buttonTestConnection.setText("Test Connection");

                if (success) {
                    showAlert("Success", "Discord connection successful! Bot is ready to send notifications.");
                } else {
                    showAlert("Error", "Failed to connect to Discord. Please check your token and channel ID.");
                }
            });
        });
    }

    private void sendTestMessage() {
        String token = textfieldDiscordToken.getText();
        String channelId = textfieldDiscordChannelId.getText();

        if (token == null || token.trim().isEmpty() || channelId == null || channelId.trim().isEmpty()) {
            showAlert("Error", "Please enter both Discord Bot Token and Channel ID");
            return;
        }

        buttonSendTestMessage.setDisable(true);
        buttonSendTestMessage.setText("Sending...");

        CompletableFuture.runAsync(() -> {
            DiscordService discordService = DiscordService.getInstance();
            
            // Initialize if not already connected
            if (!discordService.isConnected()) {
                if (!discordService.initialize(token, channelId)) {
                    Platform.runLater(() -> {
                        showAlert("Error", "Failed to connect to Discord for test message");
                        buttonSendTestMessage.setDisable(false);
                        buttonSendTestMessage.setText("Send Test Message");
                    });
                    return;
                }
            }

            // Send test message
            String testMessage = "🧪 **Test Message from WOS Bot**\nThis is a test notification to verify Discord integration is working correctly.";
            // Create a temporary profile for test message
            DTOProfiles tempProfile = new DTOProfiles(1L, "Test Profile", "0", true);
            discordService.sendNotification(testMessage, tempProfile);

            Platform.runLater(() -> {
                showAlert("Success", "Test message sent to Discord channel!");
                buttonSendTestMessage.setDisable(false);
                buttonSendTestMessage.setText("Send Test Message");
            });
        });
    }

    private void updateConnectionStatus(boolean connected) {
        if (connected) {
            circleConnectionStatus.setFill(Color.GREEN);
            labelConnectionStatus.setText("Connected");
            labelConnectionStatus.setStyle("-fx-text-fill: green;");
        } else {
            circleConnectionStatus.setFill(Color.RED);
            labelConnectionStatus.setText("Disconnected");
            labelConnectionStatus.setStyle("-fx-text-fill: red;");
        }
    }

    private void saveSettings() {
        String token = textfieldDiscordToken.getText();
        String channelId = textfieldDiscordChannelId.getText();
        String scrollInterval = textfieldScrollInterval.getText();

        // Validate required fields
        if (token == null || token.trim().isEmpty()) {
            showAlert("Error", "Please enter a Discord Bot Token");
            return;
        }

        if (channelId == null || channelId.trim().isEmpty()) {
            showAlert("Error", "Please enter a Discord Channel ID");
            return;
        }

        // Save settings to profile
        if (profileObserver != null) {
            profileObserver.notifyProfileChange(EnumConfigurationKey.DISCORD_TOKEN_STRING, token.trim());
            profileObserver.notifyProfileChange(EnumConfigurationKey.DISCORD_CHANNEL_ID_STRING, channelId.trim());
            
            // Save scroll interval if it's a valid number
            try {
                int interval = Integer.parseInt(scrollInterval.trim());
                if (interval > 0) {
                    profileObserver.notifyProfileChange(EnumConfigurationKey.DISCORD_SCROLL_INTERVAL_INT, interval);
                }
            } catch (NumberFormatException e) {
                // Use default value if invalid
                profileObserver.notifyProfileChange(EnumConfigurationKey.DISCORD_SCROLL_INTERVAL_INT, 30);
            }
        }

        showAlert("Success", "Discord settings saved successfully!");
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    @Override
    public void onProfileLoad(ProfileAux profile) {
        super.onProfileLoad(profile);
        
        // Update connection status based on current Discord service state
        DiscordService discordService = DiscordService.getInstance();
        updateConnectionStatus(discordService.isConnected());
    }
} 