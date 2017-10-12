package com.xh.common.controller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.xh.common.exception.WebException;
import com.xh.dto.UserDto;
import com.xh.entry.User;

public class BaseController {
	
	protected final Log logger = LogFactory.getLog(getClass());
	
	protected void isAdmin( User user) throws WebException{
		if(!"admin".equals(user.getPermissions().getRole())){
			 throw new WebException(" you have no permissions. ") ;
		}
	}
	
	protected void mustDev(UserDto user) throws WebException{
		if(!user.development()){
			throw new WebException("The current environment is not a development environment!");
		}
	}
	
}
