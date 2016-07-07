package com.ailk.core.exception;

public class ConnectionException extends AppRuntimeException{

	public ConnectionException(){
		
	}
	
	public ConnectionException(String message){
		super(message);
	}
	
	public ConnectionException(Throwable e){
		super(e);
	}
	
	public ConnectionException(String message, Throwable e){
		super(message, e);
	}
}
