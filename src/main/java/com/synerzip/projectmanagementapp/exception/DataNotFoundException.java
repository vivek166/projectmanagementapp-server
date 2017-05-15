package com.synerzip.projectmanagementapp.exception;

import java.io.Serializable;

public class DataNotFoundException extends Exception implements Serializable{
	private static final long serialVersionUID = 7482199285014136061L;

	public DataNotFoundException(){
		super();
	}
	public DataNotFoundException(String message){
		super(message);
	}
	public DataNotFoundException(String message, Exception e){
		super(message, e);
	}
}
