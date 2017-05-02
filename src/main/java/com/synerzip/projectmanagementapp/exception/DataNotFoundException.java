package com.synerzip.projectmanagementapp.exception;

public class DataNotFoundException extends RuntimeException{
	private static final long serialVersionUID = 7482199285014136061L;

	public DataNotFoundException(String message){
		super(message);
	}
}
