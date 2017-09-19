package com.xh.controller;

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
import com.xh.common.exception.WebException;
import com.xh.entry.User;
import com.xh.service.login.LoginService;
import com.xh.util.Base64Util;
import com.xh.util.Utils;

@Controller
@RequestMapping(Utils.API_PREFIX)
public class LoginController {
	
	@Autowired
	LoginService loginService;
	
	@RequestMapping(method=RequestMethod.POST, value="/login")
	public @ResponseBody Object login(@RequestBody User user,HttpServletResponse response) throws Exception {
		User result = loginService.login(user);
		if(result != null){
			
			Cookie cookie = new Cookie(Utils.SSO_COOKIE_NAME,Base64Util.encodeUser(result));
			cookie.setHttpOnly(true);
			cookie.setPath("/");
			response.addCookie(cookie);
			
			return result;
		}else{
			throw new WebException("认证失败");
		}
		
	}
	
	@RequestMapping(method=RequestMethod.PUT, value="/login")
	@ApiOperation(value = "api用户登录", notes = "api用户登录", httpMethod = "PUT")
	public @ResponseBody Object loginByUserPassword(@RequestParam String username,@RequestParam String password,HttpServletResponse response) throws Exception {
		User user = new User();
		user.setUsername(username);
		user.setPassword(password);
		return login(user, response) ;
		
	}

	
	@RequestMapping(method=RequestMethod.GET, value="/logout")
	public @ResponseBody Object logout(HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		Cookie cookie = new Cookie(Utils.SSO_COOKIE_NAME,"");
		cookie.setHttpOnly(true);
		cookie.setPath("/");
		response.addCookie(cookie);
		
		return "登出成功!";
	}



}
