package com.xh.common.controller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.bind.annotation.SessionAttribute;

import com.xh.common.exception.WebException;
import com.xh.entry.User;

public class BaseController {
	
	protected final Log logger = LogFactory.getLog(getClass());
	
	protected void isAdmin(@SessionAttribute User user) throws WebException{
		
		if(!"admin".equals(user.getPermissions().getRole())){
			 throw new WebException(" you have no permissions. ") ;
		}
	}
	
}
