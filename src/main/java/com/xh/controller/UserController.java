package com.xh.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttribute;

import com.xh.common.controller.BaseController;
import com.xh.entry.User;
import com.xh.service.user.UserService;
import com.xh.util.Utils;

@Controller
@RequestMapping(Utils.API_PREFIX)
public class UserController extends BaseController{
	
	@Autowired
	UserService userService;
	
	@RequestMapping(method=RequestMethod.GET,value="/user")
	public @ResponseBody Object getCurrentUser(@SessionAttribute User user ) throws Exception {
		return user;
	}
	
	@RequestMapping(method=RequestMethod.POST,value="/user")
	public @ResponseBody Object addUser(@RequestBody User user ) throws Exception {
		return userService.addUser(user);
	}

	@RequestMapping(method=RequestMethod.PATCH, value="/user/{id}")
	public @ResponseBody Object updateUser( @PathVariable("id") String id,@RequestBody User user) throws Exception {
		return userService.updateUserById(id, user);
	}

	@RequestMapping(method=RequestMethod.DELETE, value="/user/{id}")
	public @ResponseBody Object deleteUser( @PathVariable("id") String id) throws Exception {
		return userService.deleteUserById(id);
	}
	
	@RequestMapping(method=RequestMethod.DELETE, value="/users")
	public @ResponseBody Object deleteUsers(@RequestBody Map ids) throws Exception {
		userService.deleteUserByIds((List)ids.get("ids"));
		return null ;
	}

	
	@RequestMapping(method=RequestMethod.GET,value="/users")
	public @ResponseBody Object getUsers(@SessionAttribute User user,
										@RequestParam(required=false,defaultValue="1") Integer current,
										@RequestParam(required=false,defaultValue="10") Integer pageSize,
										@RequestParam(required=false,value="name") String search ) throws Exception {
		isAdmin(user);
		return userService.getUserList(current, pageSize, search);
		
		
	}

}
