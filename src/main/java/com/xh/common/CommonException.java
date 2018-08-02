package com.xh.common;

import com.xh.util.Utils;

public class CommonException extends Exception {
	
	private static final long serialVersionUID = -6087203550677392440L;
	
	private String message ;
	
	public CommonException(String message){
		super(message);
		this.message =message ;
	}
	
	public CommonException(String message,Throwable e){
		super(message,e);
		this.message =message ;
	}
	
	public CommonException(Throwable e){
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
		if(e == null || e.getClass() == null){
			return "";
		}

		StringBuilder sb = new StringBuilder(" ");

		if(e.getCause() != null){
			String resultCause = getExceptionMessage(e.getCause(),false);
			if(!Utils.isEmpty(resultCause)){
				sb.append(resultCause);
			}
		} else {

			String result = e.getMessage() != null ? e.getMessage().trim() : "";;
			if(!Utils.isEmpty(result)) { //&& !result.trim().contains("\n")){
				sb.append(result); //.split("\\n")[0].trim());
			} else {
				sb.append(e.getClass().getSimpleName());
			}
		}
		return sb.toString();
	}
	
	public static CommonException parseException(Exception e) {
		return new CommonException(e);
		
	}
	
}
