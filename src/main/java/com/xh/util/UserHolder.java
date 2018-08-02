package com.xh.util;

import org.apache.log4j.Logger;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.xh.common.CommonException;
import com.xh.dto.UserDto;
import com.xh.service.user.UserService;

public class UserHolder {
	
   private static Logger logger = Logger.getLogger(UserHolder.class);
   
   public static final String DEFAULT_USER = "system_user" ;
   
   public static final String USER_KEY = "user" ;
   
   private static final ThreadLocal<UserDto> userThreadLocal = new ThreadLocal<UserDto>();
   
   public static String getUserName( ) {
	  return getUserName(false);
   }
   
   public static String getUserName(boolean useDefault) {
	  UserDto user = getUser() ;
	  if(user != null ) {
		  return user.getUsername();
	  }
	  if(useDefault) {
		  return DEFAULT_USER;
	  }
	  return null;
   }

   public static UserDto getUser() {
	  UserDto user = null;
	  
      ServletRequestAttributes requestAttributs = (ServletRequestAttributes)RequestContextHolder.getRequestAttributes(); 
      logger.debug("servlet request requestAttributs =" + requestAttributs);
      if(requestAttributs != null) { 
         user = (UserDto) requestAttributs.getRequest().getAttribute(USER_KEY);
         if( user == null ) {
        	 user = (UserDto)  requestAttributs.getRequest().getSession().getAttribute(USER_KEY);
         }
      }
      if(user == null) {
    	  user = getUserFromThread();
      }
      return user;
   }

   public static void setUser(String username)  {
	   
	    UserDto user = null;
		try {
			UserService userService =  (UserService) SpringUtil.getBean(UserService.class);
			   user = userService.getUserByUserName(username);
			   if(user == null ) {
				   user =  userService.getUserById(username);
			   }
		} catch (CommonException e) {
		}
	    userThreadLocal.set(user);// 112
	}


   public static void setUser( UserDto user) {
	   userThreadLocal.set(user);// 112
	}
   
   public static void remove() {
	   userThreadLocal.remove();
   }

   public static UserDto getUserFromThread( ) {
      return (UserDto)userThreadLocal.get();// 100
   }

}