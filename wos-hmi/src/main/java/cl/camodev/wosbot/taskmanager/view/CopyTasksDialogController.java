package cl.camodev.wosbot.taskmanager.view;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cl.camodev.wosbot.ot.DTOProfiles;
import cl.camodev.wosbot.taskmanager.controller.TaskManagerActionController;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class CopyTasksDialogController {

    private final Map<CheckBox, DTOProfiles> profileMap = new HashMap<>();
    private DTOProfiles sourceProfile;
    private TaskManagerActionController actionController;

    @FXML
    private Label lblSourceProfile;

    @FXML
    private VBox vboxProfileList;

    @FXML
    private Button btnSelectAll;

    @FXML
    private Button btnDeselectAll;

    public void init(DTOProfiles sourceProfile, TaskManagerActionController actionController) {
        this.sourceProfile = sourceProfile;
        this.actionController = actionController;
        lblSourceProfile.setText("Copy tasks from: " + sourceProfile.getName());
        loadProfiles();
    }

    private void loadProfiles() {
        List<DTOProfiles> profiles = actionController.getAllProfiles();
        for (DTOProfiles profile : profiles) {
            if (profile.getId().equals(sourceProfile.getId())) {
                continue;
            }
            CheckBox cb = new CheckBox(profile.getName());
            vboxProfileList.getChildren().add(cb);
            profileMap.put(cb, profile);
        }
    }

    @FXML
    private void handleSelectAll() {
        profileMap.keySet().forEach(cb -> cb.setSelected(true));
    }

    @FXML
    private void handleDeselectAll() {
        profileMap.keySet().forEach(cb -> cb.setSelected(false));
    }

    @FXML
    private void handleCancel() {
        close();
    }

    @FXML
    private void handleCopy() {
        List<DTOProfiles> selected = new ArrayList<>();
        profileMap.forEach((cb, profile) -> {
            if (cb.isSelected()) {
                selected.add(profile);
            }
        });
        if (!selected.isEmpty()) {
            actionController.copyTasksToProfiles(sourceProfile, selected);
        }
        close();
    }

    private void close() {
        Stage stage = (Stage) lblSourceProfile.getScene().getWindow();
        stage.close();
    }
}
