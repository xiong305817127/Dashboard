package com.xh.service.common;

import com.xh.common.CommonException;

public class ServiceException extends CommonException {
	
	private static final long serialVersionUID = 6858715739767368072L;

	public ServiceException(String message){
		super(message);
	}
	
	public ServiceException(String message,Throwable e){
		super(message,e);
	}
	
	public ServiceException(Throwable e){
		super(e);
	}
	
}
