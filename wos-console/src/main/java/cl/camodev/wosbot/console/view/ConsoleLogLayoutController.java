package cl.camodev.wosbot.console.view;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import cl.camodev.wosbot.console.model.EnumTpMessageSeverity;
import cl.camodev.wosbot.console.model.LogMessageAux;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

public class ConsoleLogLayoutController {

	@FXML
	private Button buttonClearLogs;

	@FXML
	private CheckBox checkboxDebug;

	@FXML
	private TableView<LogMessageAux> tableviewLogMessages;

	@FXML
	private TableColumn<LogMessageAux, String> columnMessage;

	@FXML
	private TableColumn<LogMessageAux, String> columnTimeStamp;

	private ObservableList<LogMessageAux> logMessages;

	@FXML
	private void initialize() {
		logMessages = FXCollections.observableArrayList();
		columnTimeStamp.setCellValueFactory(cellData -> cellData.getValue().timeStampProperty());
		columnMessage.setCellValueFactory(cellData -> cellData.getValue().messageProperty());
		tableviewLogMessages.setItems(logMessages);
	}

	@FXML
	void handleButtonClearLogs(ActionEvent event) {

		logMessages.clear();
	}

	public void appendMessage(EnumTpMessageSeverity severity, String message) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
		String formattedDate = LocalDateTime.now().format(formatter);

		logMessages.add(new LogMessageAux(formattedDate, severity, message));

		if (logMessages.size() > 200) {
			logMessages.remove(0);
		}
	}

}
