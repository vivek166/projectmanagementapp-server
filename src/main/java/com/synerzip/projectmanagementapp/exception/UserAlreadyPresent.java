package com.synerzip.projectmanagementapp.exception;

public class UserAlreadyPresent extends RuntimeException {

	private static final long serialVersionUID = -6165662360812486329L;

	public UserAlreadyPresent(String message) {
		super(message);
	}

}
