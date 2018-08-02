package com.xh.webservice;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

import com.xh.common.CommonException;
import com.xh.dto.UserDto;

@WebService(targetNamespace="UserWebServiceNamespace" )
public interface UserWebService {
	
	@WebMethod
	public UserDto getUser(@WebParam(name="name")String userId )  throws CommonException ;
	
}
