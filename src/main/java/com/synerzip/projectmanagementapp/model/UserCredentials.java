package com.synerzip.projectmanagementapp.model;

public class UserCredentials {

	private String userName;
	private String userPassword;

	public UserCredentials() {
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getUserPassword() {
		return userPassword;
	}

	public void setUserPassword(String userPassword) {
		this.userPassword = userPassword;
	}

	@Override
	public String toString() {
		return "UserCredentials [userName=" + userName + ", userPassword=" + userPassword + "]";
	}

}
