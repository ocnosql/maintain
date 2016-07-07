package com.ailk.core.exception;

public class AppRuntimeException extends RuntimeException{

	public AppRuntimeException(){
		
	}
	
	public AppRuntimeException(String message){
		super(message);
	}
	
	public AppRuntimeException(Throwable e){
		super(e);
	}
	
	public AppRuntimeException(String message, Throwable e){
		super(message, e);
	}
	
}
