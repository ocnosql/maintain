package com.ailk.util;

import java.io.IOException;
import java.util.Map;

import net.sf.json.JSONObject;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.URI;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.RequestEntity;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class RestUtil {
	
	private static int _timeout=  20 * 1000;
	private static final String CONTENT_CHARSET = "UTF-8";
	private static Log log = LogFactory.getLog(RestUtil.class);

	public static String postWithJson(String uri, Map paramMap) throws IOException{
		String data= "";
		HttpClient httpClient= new HttpClient();
		httpClient.setConnectionTimeout(_timeout);
		httpClient.setTimeout(_timeout);
		
		PostMethod method = null;
		try{
			method= new PostMethod();
			method.setURI(new URI(uri, false));
			RequestEntity entity = new org.apache.commons.httpclient.methods.StringRequestEntity(JSONObject.fromObject(paramMap).toString(), "text", "UTF-8");
                        method.setRequestEntity(entity);
			int statusCode = httpClient.executeMethod(method);
			if (statusCode != HttpStatus.SC_OK) {
				  log.error("Method failed: " + method.getStatusLine());
			}
			data= new String(method.getResponseBody(),"utf-8");
                        log.debug("recv date from http : ["+data+"]");
		}catch(HttpException e){
			log.error("Please check your provided http address!");
		}catch(IOException e){
			log.error(e.getMessage());
		}catch(Exception e){
			log.error(e.getMessage());
		}finally{
			if(method!=null) method.releaseConnection();
		}
		return data;
	}
	
	
	public static String post(String uri, String content, String charSet) throws IOException{
		String data= "";
		HttpClient httpClient= new HttpClient();
		httpClient.setConnectionTimeout(_timeout);
		httpClient.setTimeout(_timeout);
		log.debug("uri:" + uri);
		PostMethod method = null;
		try{
			method = new PostMethod();
			method.setURI(new URI(uri, false));
			RequestEntity entity = new org.apache.commons.httpclient.methods.StringRequestEntity(content, "text", "UTF-8");
                        method.setRequestEntity(entity);
			int statusCode = httpClient.executeMethod(method);
			if (statusCode != HttpStatus.SC_OK) {
				  log.error("Method failed: " + method.getStatusLine());
				  
				  //return method.getStatusLine().toString();
			}
			data = new String(method.getResponseBody(), charSet);
                        log.debug("recv date from http : ["+data+"]");
		}catch(HttpException e){
			log.error("Please check your provided http address!");
		}catch(IOException e){
			log.error(e.getMessage());
			throw e;
		}catch(Exception e){
			log.error(e.getMessage());
		}finally{
			if(method!=null) method.releaseConnection();
		}
		return data;
	}
	
	
	
	public static Response postURL(String uri, String content, String charSet) throws IOException{
		Response response = new Response(); 
		String data= "";
		HttpClient httpClient= new HttpClient();
		httpClient.setConnectionTimeout(_timeout);
		httpClient.setTimeout(_timeout);
		log.debug("uri:" + uri);
		PostMethod method = null;
		try{
			method = new PostMethod();
			method.setURI(new URI(uri, false));
			RequestEntity entity = new org.apache.commons.httpclient.methods.StringRequestEntity(content, "text", "UTF-8");
                        method.setRequestEntity(entity);
			int statusCode = httpClient.executeMethod(method);
			if (statusCode != HttpStatus.SC_OK) {	
				log.error("Method failed: " + method.getStatusLine());	
				response.setSuccess(false);	
				response.setHttpStatus(statusCode);	
			} else {
				response.setSuccess(true);		
			}
			data = new String(method.getResponseBody(), charSet);	
            log.debug("recv date from http : ["+data+"]");
            response.setReturnMsg(data);
		}catch(HttpException e){
			log.error("Please check your provided http address!");
		}catch(IOException e){
			log.error(e.getMessage());
			throw e;
		}catch(Exception e){
			log.error(e.getMessage());
		}finally{
			if(method!=null) method.releaseConnection();
		}
		return response;
	}
	
	
	public static class Response {
		private boolean isSuccess;
		private String returnMsg;
		private int httpStatus;
		public boolean isSuccess() {
			return isSuccess;
		}
		public void setSuccess(boolean isSuccess) {
			this.isSuccess = isSuccess;
		}
		public String getReturnMsg() {
			return returnMsg;
		}
		public void setReturnMsg(String returnMsg) {
			this.returnMsg = returnMsg;
		}
		public int getHttpStatus() {
			return httpStatus;
		}
		public void setHttpStatus(int httpStatus) {
			this.httpStatus = httpStatus;
		}
		
	}
	
	
	
	
	
	public static void main(String[] args) throws IOException{
		String json = "{\"pageIndex\":\"1\",\"pageSize\":\"3000\",\"billType\":\"0\",\"fromDate\":\"20130301\",\"thruDate\":\"20130331\",\"billId\":\"18601134210\",\"impType\":\"CUR\",\"queryType\":\"00\",\"userId\":\"\",\"regionCode\":\"571\",\"dbType\":\"ocnosqlDataSource\"}";
		System.out.println(json);
		RestUtil.post("http://localhost:8080/DRQuery/query/common", json, "UTF-8");
		
	}
}
