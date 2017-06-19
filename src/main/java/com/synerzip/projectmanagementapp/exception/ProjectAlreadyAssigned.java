package com.synerzip.projectmanagementapp.exception;

public class ProjectAlreadyAssigned extends RuntimeException {

	private static final long serialVersionUID = 497887304161285067L;
	public ProjectAlreadyAssigned(String message){
		super(message);
	}
}
