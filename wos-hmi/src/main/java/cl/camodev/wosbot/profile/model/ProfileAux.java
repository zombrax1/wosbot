package cl.camodev.wosbot.profile.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import cl.camodev.wosbot.console.enumerable.EnumConfigurationKey;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.LongProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class ProfileAux {

	private LongProperty id;
	private StringProperty name;
	private StringProperty emulatorNumber;
	private BooleanProperty enabled;
	private StringProperty status;

	private List<ConfigAux> configs = new ArrayList<ConfigAux>();

	// Constructor vacío
	public ProfileAux() {
		this.id = new SimpleLongProperty();
		this.name = new SimpleStringProperty();
		this.emulatorNumber = new SimpleStringProperty();
		this.enabled = new SimpleBooleanProperty();
		this.status = new SimpleStringProperty();
	}

	// Constructor con parámetros
	public ProfileAux(Long id, String name, String emulatorNumber, boolean enabled, String status) {
		this.id = new SimpleLongProperty(id);
		this.name = new SimpleStringProperty(name);
		this.emulatorNumber = new SimpleStringProperty(emulatorNumber);
		this.enabled = new SimpleBooleanProperty(enabled);
		this.status = new SimpleStringProperty(status);
	}

	// Métodos para la propiedad 'id'
	public Long getId() {
		return id.get();
	}

	public void setId(Long id) {
		this.id.set(id);
	}

	public LongProperty idProperty() {
		return id;
	}

	// Métodos para la propiedad 'name'
	public String getName() {
		return name.get();
	}

	public void setName(String name) {
		this.name.set(name);
	}

	public StringProperty nameProperty() {
		return name;
	}

	// Métodos para la propiedad 'emulatorNumber'
	public String getEmulatorNumber() {
		return emulatorNumber.get();
	}

	public void setEmulatorNumber(String emulatorNumber) {
		this.emulatorNumber.set(emulatorNumber);
	}

	public StringProperty emulatorNumberProperty() {
		return emulatorNumber;
	}

	// Métodos para la propiedad 'enabled'
	public Boolean isEnabled() {
		return enabled.get();
	}

	public void setEnabled(boolean enabled) {
		this.enabled.set(enabled);
	}

	public BooleanProperty enabledProperty() {
		return enabled;
	}

	// Métodos para la propiedad 'status'
	public String getStatus() {
		return status.get();
	}

	public void setStatus(String status) {
		this.status.set(status);
	}

	public StringProperty statusProperty() {
		return status;
	}

	public List<ConfigAux> getConfigs() {
		return configs;
	}

	public void setConfigs(List<ConfigAux> configs) {
		this.configs = configs;
	}

	/**
	 * Obtiene el valor de una configuración específica utilizando EnumConfigurationKey. Es un método genérico que devuelve el tipo correcto
	 * basado en la clave.
	 */
	public <T> T getConfig(EnumConfigurationKey key, Class<T> clazz) {
		Optional<ConfigAux> configOptional = configs.stream().filter(config -> config.getName().equalsIgnoreCase(key.name())).findFirst();

		if (!configOptional.isPresent()) {

			ConfigAux defaultConfig = new ConfigAux(key.name(), key.getDefaultValue());
			configs.add(defaultConfig);
		}
		String valor = configOptional.map(ConfigAux::getValue).orElse(key.getDefaultValue());

		return key.castValue(valor);
	}

	public <T> void setConfig(EnumConfigurationKey key, T value) {
		String valorAAlmacenar = value.toString();
		Optional<ConfigAux> configOptional = configs.stream().filter(config -> config.getName().equalsIgnoreCase(key.name())).findFirst();

		if (configOptional.isPresent()) {
			configOptional.get().setValue(valorAAlmacenar);
		} else {
			ConfigAux nuevaConfig = new ConfigAux(key.name(), valorAAlmacenar);
			configs.add(nuevaConfig);
		}
	}
}
