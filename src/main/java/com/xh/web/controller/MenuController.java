package com.xh.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttribute;

import com.xh.common.CommonException;
import com.xh.dto.MenuDto;
import com.xh.dto.UserDto;
import com.xh.service.menu.MenuService;
import com.xh.util.Utils;
import com.xh.web.common.BaseController;

@Controller
@RequestMapping(Utils.API_PREFIX)
public class MenuController extends BaseController{
	
	@Autowired
	MenuService menuService;
	
	@RequestMapping(method=RequestMethod.GET,value="/menus")
	public @ResponseBody Object getUser( ) throws  CommonException {

		return menuService.getMenuList();
		
	}

	@RequestMapping(method=RequestMethod.PATCH , value="/menu/{id}")
	public @ResponseBody Object updateMemu( @PathVariable("id") String id, @RequestBody MenuDto menu ,@SessionAttribute UserDto user) throws  CommonException {
		
		isAdmin(user);
		
		return menuService.updateMenuByKey(id,menu);
	}

	@RequestMapping(method=RequestMethod.DELETE, value="/menu/{id}")
	public @ResponseBody Object deleteMemu( @PathVariable("id") String id,@SessionAttribute UserDto user ) throws  CommonException {
		isAdmin(user);
		mustDev(user);
		
		return menuService.deleteMenuByKey(id);
	}

	@RequestMapping(method=RequestMethod.POST,value="/menus")
	public @ResponseBody Object addMenu(@RequestBody MenuDto menu ,@SessionAttribute UserDto user ) throws  CommonException {
		
		isAdmin(user);
		mustDev(user);
		
		menuService.addMenu(menu);
		return menu;
	}
	

	
}
