package com.xh.web.common;

import com.xh.common.CommonException;

public class WebException extends CommonException {
	
	private static final long serialVersionUID = 6858715739767368072L;

	public WebException(String message){
		super(message);
	}
	
	public WebException(String message,Throwable e){
		super(message,e);
	}
	
	public WebException(Throwable e){
		super(e);
	}
	
}
