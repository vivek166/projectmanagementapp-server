package com.synerzip.projectmanagementapp.exception;

public class CanNotEmptyField extends RuntimeException {

	private static final long serialVersionUID = -3267287860818455504L;

	public CanNotEmptyField(String message) {
		super(message);
	}

}