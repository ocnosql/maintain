package com.ailk.pool;

import com.ailk.core.exception.AppRuntimeException;

public class PoolException extends AppRuntimeException{

	public PoolException(){
		
	}
	
	public PoolException(String message){
		super(message);
	}
	
	public PoolException(Throwable e){
		super(e);
	}
	
	public PoolException(String message, Throwable e){
		super(message, e);
	}
}
