package com.synerzip.projectmanagementapp.exception;

public class CompanyAlreadyPresent extends RuntimeException{

	private static final long serialVersionUID = -7094349596111221204L;
	
	public CompanyAlreadyPresent(String message){
		super(message);
	}

}
