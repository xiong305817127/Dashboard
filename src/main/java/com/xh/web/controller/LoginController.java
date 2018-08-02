package com.xh.web.controller;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.wordnik.swagger.annotations.ApiOperation;
import com.xh.common.CommonException;
import com.xh.dto.UserDto;
import com.xh.service.login.LoginService;
import com.xh.util.Base64Util;
import com.xh.util.UserHolder;
import com.xh.util.Utils;
import com.xh.web.common.WebException;

@Controller
@RequestMapping(Utils.API_PREFIX)
public class LoginController {
	
	@Autowired
	LoginService loginService;
	
	@RequestMapping(method=RequestMethod.POST, value="/login")
	public @ResponseBody Object login(@RequestBody UserDto user,HttpServletResponse response) throws CommonException {
		try{
			UserDto result = loginService.login(user);
			if(result != null){
				
				Cookie cookie = new Cookie(Utils.SSO_COOKIE_NAME,Base64Util.encodeUser(result));
				cookie.setHttpOnly(true);
				cookie.setPath("/");
				response.addCookie(cookie);
				
				return result;
			}else{
				throw new WebException("认证失败");
			}
		}catch(Exception e){
			throw new WebException("认证失败");
		}
		
	}
	
	@RequestMapping(method=RequestMethod.PUT, value="/login")
	@ApiOperation(value = "api用户登录", notes = "api用户登录", httpMethod = "PUT")
	public @ResponseBody Object loginByUserPassword(@RequestParam String username,@RequestParam String password,HttpServletResponse response) throws CommonException {
		UserDto user = new UserDto();
		user.setUsername(username);
		user.setPassword(password);
		return login(user, response) ;
		
	}

	
	@RequestMapping(method=RequestMethod.GET, value="/logout")
	public @ResponseBody Object logout(HttpServletRequest request, HttpServletResponse response) throws CommonException {
		
		Cookie cookie = new Cookie(Utils.SSO_COOKIE_NAME,"");
		cookie.setHttpOnly(true);
		cookie.setPath("/");
		response.addCookie(cookie);
		
		UserHolder.remove();
		
		return "登出成功!";
	}



}
