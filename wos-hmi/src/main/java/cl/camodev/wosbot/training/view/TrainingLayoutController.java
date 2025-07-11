package cl.camodev.wosbot.training.view;

import cl.camodev.wosbot.common.view.AbstractProfileController;
import cl.camodev.wosbot.console.enumerable.EnumConfigurationKey;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;

public class TrainingLayoutController extends AbstractProfileController {

	@FXML
	private CheckBox checkBoxTrainInfantry;

	@FXML
	private CheckBox checkBoxTrainLancers;

	@FXML
	private CheckBox checkBoxTrainMarksman;

	@FXML
	private CheckBox checkBoxUseResources;

	@FXML
	private void initialize() {
		checkBoxMappings.put(checkBoxTrainInfantry, EnumConfigurationKey.TRAIN_INFANTRY_BOOL);
		checkBoxMappings.put(checkBoxTrainLancers, EnumConfigurationKey.TRAIN_LANCER_BOOL);
		checkBoxMappings.put(checkBoxTrainMarksman, EnumConfigurationKey.TRAIN_MARKSMAN_BOOL);
		checkBoxMappings.put(checkBoxUseResources, EnumConfigurationKey.BOOL_TRAINING_RESOURCES);

		initializeChangeEvents();
		checkBoxUseResources.setDisable(true);
	}
}
