package com.ailk.core.exception;

public class ScriptRuntimeException extends AppRuntimeException{

	public ScriptRuntimeException(){
		
	}
	
	public ScriptRuntimeException(String message){
		super(message);
	}
	
	public ScriptRuntimeException(Throwable e){
		super(e);
	}
	
	public ScriptRuntimeException(String message, Throwable e){
		super(message, e);
	}
}
