package cl.camodev.wosbot.ot;

public class DTOConfig {

	private String profileName;
	private Integer emulatorNumber;

	private Boolean nomadicMerchant;
	private Boolean nomadicMerchantVipPoints;

	private Boolean mysteryShopFreePackage;

	public String getProfileName() {
		return profileName;
	}

	public void setProfileName(String profileName) {
		this.profileName = profileName;
	}

	public Integer getEmulatorNumber() {
		return emulatorNumber;
	}

	public void setEmulatorNumber(Integer emulatorNumber) {
		this.emulatorNumber = emulatorNumber;
	}

	public Boolean getNomadicMerchant() {
		return nomadicMerchant;
	}

	public void setNomadicMerchant(Boolean nomadicMerchant) {
		this.nomadicMerchant = nomadicMerchant;
	}

	public Boolean getNomadicMerchantVipPoints() {
		return nomadicMerchantVipPoints;
	}

	public void setNomadicMerchantVipPoints(Boolean nomadicMerchantVipPoints) {
		this.nomadicMerchantVipPoints = nomadicMerchantVipPoints;
	}

	public Boolean getMysteryShopFreePackage() {
		return mysteryShopFreePackage;
	}

	public void setMysteryShopFreePackage(Boolean mysteryShopFreePackage) {
		this.mysteryShopFreePackage = mysteryShopFreePackage;
	}

}
