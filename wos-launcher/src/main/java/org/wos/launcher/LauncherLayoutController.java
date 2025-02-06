package org.wos.launcher;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.wos.launcher.controller.LauncherActionController;

import cl.camodev.wosbot.city.view.CityEventsLayoutController;
import cl.camodev.wosbot.console.enumerable.EnumTpMessageSeverity;
import cl.camodev.wosbot.console.view.ConsoleLogLayoutController;
import cl.camodev.wosbot.ot.DTOConfig;
import cl.camodev.wosbot.ot.OTProfiles;
import cl.camodev.wosbot.pets.view.PetsLayoutController;
import cl.camodev.wosbot.shop.view.ShopLayoutController;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;

public class LauncherLayoutController {

	private LauncherActionController actionController;

	private ConsoleLogLayoutController consoleLogLayoutController;

	private ShopLayoutController shopLayoutController;

	private PetsLayoutController petsLayoutController;

	private CityEventsLayoutController cityEventsLayoutController;

	@FXML
	private Button buttonDock;

	@FXML
	private Button buttonHideShow;

	@FXML
	private Button buttonPause;

	@FXML
	private Button buttonPhoto;

	@FXML
	private Button buttonStartStop;

	@FXML
	private TabPane tabPaneFuntions;

	@FXML
	private void initialize() {
		actionController = new LauncherActionController();
		List<OTProfiles> profiles = actionController.getProfiles();

		consoleLogLayoutController = new ConsoleLogLayoutController();
		addTab("ConsoleLogLayout", "Logs", consoleLogLayoutController);

		consoleLogLayoutController.appendMessage(EnumTpMessageSeverity.INFO, "Loading Modules");
		shopLayoutController = new ShopLayoutController();
		addTab("ShopLayoutLayout", "Shop", shopLayoutController);

		petsLayoutController = new PetsLayoutController();
		addTab("PetsLayout", "Pets", petsLayoutController);

	}

	@FXML
	void handleButtonDock(ActionEvent event) {

	}

	@FXML
	void handleButtonHideShow(ActionEvent event) {

	}

	@FXML
	void handleButtonPause(ActionEvent event) {

	}

	@FXML
	void handleButtonPhoto(ActionEvent event) {

	}

	@FXML
	void handleButtonStartStop(ActionEvent event) {
		List<DTOConfig> configs = new ArrayList<DTOConfig>();

		DTOConfig dtoConfig = new DTOConfig();
		dtoConfig.setProfileName("Vici");
		dtoConfig.setEmulatorNumber(0);
		dtoConfig.setNomadicMerchant(true);
		dtoConfig.setNomadicMerchantVipPoints(true);
		dtoConfig.setMysteryShopFreePackage(true);
		configs.add(dtoConfig);

		actionController.startBot(configs);
		try {
//			actionController.startBot();
//
//			Thread.sleep(25);
//			consoleLogLayoutController.appendMessage(EnumTpMessageSeverity.INFO, "Loading Profiles");
//			Thread.sleep(25);
//			consoleLogLayoutController.appendMessage(EnumTpMessageSeverity.INFO, "Version 1.0");
//			consoleLogLayoutController.appendMessage(EnumTpMessageSeverity.INFO, "Ready to Start");

		} catch (Exception e) {
			// TODO: handle exception
		}

	}

	private void addTab(String fxmlName, String title, Object controller) {
		try {
			FXMLLoader loader = new FXMLLoader(controller.getClass().getResource(fxmlName + ".fxml"));
			loader.setController(controller);
			Parent root = loader.load();

			Tab newTab = new Tab(title);
			newTab.setContent(root);
			newTab.setClosable(false);
			newTab.setGraphic(new Label());
			Platform.runLater(() -> {
				// Get the "tab-container" node. This is what we want to rotate/shift for easy
				// left-alignment.
				// You can omit the last "getParent()" with a few tweaks for centered labels
				Parent tabContainer = newTab.getGraphic().getParent().getParent();
				tabContainer.setRotate(90);
				// By default the display will originate from the center.
				// Applying a negative Y transformation will move it left.
				// Should be the 'TabMinHeight/2'
				tabContainer.setTranslateY(-75);
			});
			tabPaneFuntions.getTabs().add(newTab);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
