package com.synerzip.projectmanagementapp.exception;

public class CanNotChangePassword extends RuntimeException {

	private static final long serialVersionUID = -1009238095199257153L;

	public CanNotChangePassword(String message) {
		super(message);
	}
}
