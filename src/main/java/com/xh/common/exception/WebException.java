package com.xh.common.exception;

import com.xh.util.Utils;

public class WebException extends Exception {
	
	private static final long serialVersionUID = 4939434368664304560L;
	
	private String message ;
	
	public WebException(String message){
		super(message);
		this.message =message ;
	}
	
	public WebException(String message,Throwable e){
		super(message,e);
		this.message =message ;
	}
	
	public WebException(Throwable e){
		super(e);
		message = getExceptionMessage(e);
	}
	
	@Override
	public String getMessage() {
		return message;
	}

	public static String getExceptionMessage(Throwable e){
		String errmsg = getExceptionMessage(e,true);
		return  Utils.isEmpty(errmsg)?"系统未知异常,请联系管理员!":"系统异常["+errmsg+"]";
	}

	private static String getExceptionMessage(Throwable e,boolean isRoot){
		if(e == null){
			return "";
		}
		String result = e.getMessage();
		if(!Utils.isEmpty(result) && !result.trim().contains("\n")){
			return result.trim();
		}
		if( e.getCause() != null){
			String resultCause = getExceptionMessage(e.getCause(),false);
			if(!Utils.isEmpty(resultCause)){
				return resultCause;
			}
		}
		if(!isRoot){
			return null;
		}
		e.printStackTrace();
		return  "" ;
	}
	
}
