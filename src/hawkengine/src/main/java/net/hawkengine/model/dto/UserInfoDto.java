package net.hawkengine.model.dto;

public class UserInfoDto {

	private String email;

	private Boolean isRegistered;

	private String loginProvider;

	public String getEmail() {
		return this.email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Boolean isRegistered() {
		return this.isRegistered;
	}

	public void setRegistered(Boolean registered) {
		this.isRegistered = registered;
	}

	public String getLoginProvider() {
		return this.loginProvider;
	}

	public void setLoginProvider(String loginProvider) {
		this.loginProvider = loginProvider;
	}
}
