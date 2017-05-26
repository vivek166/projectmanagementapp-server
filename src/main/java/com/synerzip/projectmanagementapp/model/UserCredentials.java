package com.synerzip.projectmanagementapp.model;

public class UserCredentials {

	private String userId;
	private String userPassword;

	public UserCredentials() {
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getUserPassword() {
		return userPassword;
	}

	public void setUserPassword(String userPassword) {
		this.userPassword = userPassword;
	}

	public String toString() {
		return "UserCredentials [userId=" + userId + ", userPassword=" + userPassword + "]";
	}

}
