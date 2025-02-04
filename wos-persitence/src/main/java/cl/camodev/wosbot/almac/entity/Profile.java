package cl.camodev.wosbot.almac.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "profiles")
public class Profile {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", nullable = false, unique = true)
	private Long id;

	@Column(name = "profile_name", nullable = false)
	private String name;

	@Column(name = "emulator_number", nullable = false)
	private Long emulatorNumber;

	@Column(name = "enabled", nullable = false)
	private Boolean enabled;

	// Getters y Setters
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Long getEmulatorNumber() {
		return emulatorNumber;
	}

	public void setEmulatorNumber(Long emulatorNumber) {
		this.emulatorNumber = emulatorNumber;
	}

	public Boolean getEnabled() {
		return enabled;
	}

	public void setEnabled(Boolean enabled) {
		this.enabled = enabled;
	}
}
