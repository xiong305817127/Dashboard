package com.xh.webservice;

import javax.jws.WebService;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.xh.common.CommonException;
import com.xh.dto.UserDto;
import com.xh.service.user.UserService;

@WebService(targetNamespace="UserWebServiceNamespace")
public class UserWebServiceImpl implements UserWebService {

	public static final Log  logger = LogFactory.getLog("UserWebService");
	
	@Autowired
	UserService userService;

	@Override
	public UserDto getUser(String userId) throws CommonException {
		return userService.getUserById(userId);
	}
	
	


}
