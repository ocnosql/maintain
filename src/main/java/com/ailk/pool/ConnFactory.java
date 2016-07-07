package com.ailk.pool;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.pool.PoolableObjectFactory;

public class ConnFactory implements PoolableObjectFactory {
	
	private static Log log = LogFactory.getLog(ConnFactory.class);
	
	private ConnHolder holder;
	
	public ConnHolder getHolder() {
		return holder;
	}

	public void setHolder(ConnHolder holder) {
		this.holder = holder;
	}
	
	private ConnFactory(){
		
	}
	
	public ConnFactory(ConnHolder holder){
		this.holder = holder;
	}

	/**
	 * 创建对象实例。同时可以分配这个对象适用的资源。
	 * **/
	public Object makeObject() throws Exception {
		log.info("make new connection...");
		return holder.makeObject();
	};

	/**
	 * 销毁对象，实际上提供了释放对象占用资源的接口。 destroyObject is invoked on every instance when
	 * it is being "dropped" from the pool (whether due to the response from
	 * validateObject, or for reasons specific to the pool implementation.)
	 * */
	public void destroyObject(Object obj) throws Exception {
		log.info("destroy connection...");
		holder.destroyObject(obj);
	}

	/***
	 * 这个方法一般在 activateObject 方法执行后调用 检查对象的有效性 validateObject is invoked in an
	 * implementation-specific fashion to determine if an instance is still
	 * valid to be returned by the pool. It will only be invoked on an
	 * "activated" instance.
	 * **/
	@Override
	public boolean validateObject(Object obj) {
		return holder.validateObject(obj);
	}

	/**
	 * 激活一个对象。 activateObject is invoked on every instance before it is returned
	 * from the pool.
	 * **/
	@Override
	public void activateObject(Object obj) throws Exception {
		//System.out.println("activateObject...");
		holder.activateObject(obj);
	}

	/**
	 * 挂起一个对象 passivateObject is invoked on every instance when it is returned
	 * to the pool
	 * **/
	public void passivateObject(Object obj) throws Exception {
		//System.out.println("passivateObject... " + obj);
		holder.passivateObject(obj);
	}
	
}
