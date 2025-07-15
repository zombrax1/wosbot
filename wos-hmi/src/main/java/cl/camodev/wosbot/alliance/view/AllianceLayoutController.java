package cl.camodev.wosbot.alliance.view;

import cl.camodev.wosbot.common.view.AbstractProfileController;
import cl.camodev.wosbot.console.enumerable.EnumConfigurationKey;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.scene.control.ComboBox;

public class AllianceLayoutController extends AbstractProfileController {

	@FXML
	private CheckBox checkBoxAutojoin, checkBoxChests, checkBoxTechContribution, checkBoxHelpRequests, checkBoxTriumph, checkBoxAlliesEssence;

	@FXML
	private TextField textfieldAutojoinQueues, textfieldChestOffset, textfieldTechOffset, textfieldHelpRequstOffset, textfieldTriumphOffset, textfieldAlliesEssenceOffsett;

	@FXML
	private void initialize() {
		checkBoxMappings.put(checkBoxAutojoin, EnumConfigurationKey.ALLIANCE_AUTOJOIN_BOOL);
		checkBoxMappings.put(checkBoxChests, EnumConfigurationKey.ALLIANCE_CHESTS_BOOL);
		checkBoxMappings.put(checkBoxTechContribution, EnumConfigurationKey.ALLIANCE_TECH_BOOL);
		checkBoxMappings.put(checkBoxHelpRequests, EnumConfigurationKey.ALLIANCE_HELP_REQUESTS_BOOL);
		checkBoxMappings.put(checkBoxTriumph, EnumConfigurationKey.ALLIANCE_TRIUMPH_BOOL);
		checkBoxMappings.put(checkBoxAlliesEssence, EnumConfigurationKey.ALLIANCE_LIFE_ESSENCE_BOOL);

		textFieldMappings.put(textfieldAutojoinQueues, EnumConfigurationKey.ALLIANCE_AUTOJOIN_QUEUES_INT);
		textFieldMappings.put(textfieldChestOffset, EnumConfigurationKey.ALLIANCE_CHESTS_OFFSET_INT);
		textFieldMappings.put(textfieldTechOffset, EnumConfigurationKey.ALLIANCE_TECH_OFFSET_INT);
		textFieldMappings.put(textfieldTriumphOffset, EnumConfigurationKey.ALLIANCE_TRIUMPH_OFFSET_INT);
		textFieldMappings.put(textfieldAlliesEssenceOffsett, EnumConfigurationKey.ALLIANCE_LIFE_ESSENCE_OFFSET_INT);
		textFieldMappings.put(textfieldHelpRequstOffset, EnumConfigurationKey.ALLIANCE_HELP_REQUESTS_OFFSET_INT);

		initializeChangeEvents();
	}
}
