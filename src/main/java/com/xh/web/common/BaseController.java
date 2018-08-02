package com.xh.web.common;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.xh.web.common.WebException;
import com.xh.common.CommonException;
import com.xh.dto.UserDto;

public class BaseController {
	
	protected final Log logger = LogFactory.getLog(getClass());
	
	protected void isAdmin( UserDto user) throws CommonException{
		if(!"admin".equals(user.getPermissions().getRole())){
			 throw new WebException(" you have no permissions. ") ;
		}
	}
	
	protected void mustDev(UserDto user) throws CommonException{
		if(!user.development()){
			throw new WebException("The current environment is not a development environment!");
		}
	}
	
}
