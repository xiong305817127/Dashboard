package com.xh.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttribute;

import com.xh.common.controller.BaseController;
import com.xh.common.exception.WebException;
import com.xh.entry.Menu;
import com.xh.entry.User;
import com.xh.service.menu.MenuService;
import com.xh.service.user.UserService;
import com.xh.util.Utils;

@Controller
@RequestMapping(Utils.API_PREFIX)
public class MenuController extends BaseController{
	
	@Autowired
	MenuService menuService;
	
	@RequestMapping(method=RequestMethod.GET,value="/menus")
	public @ResponseBody Object getUser(@SessionAttribute User user ) throws Exception {

		return menuService.getMenuList();
		
	}

	@RequestMapping(method=RequestMethod.PATCH , value="/menu/{id}")
	public @ResponseBody Object updateMemu( @PathVariable("id") String id, @RequestBody Menu menu) throws Exception {
		return menuService.updateMenuByKey(id,menu);
	}

	@RequestMapping(method=RequestMethod.DELETE, value="/menu/{id}")
	public @ResponseBody Object deleteMemu( @PathVariable("id") String id) throws Exception {
		return menuService.deleteMenuByKey(id);
	}

	@RequestMapping(method=RequestMethod.POST,value="/menus")
	public @ResponseBody Object addMenu(@RequestBody Menu menu ) throws Exception {
		menuService.addMenu(menu);
		return menu;
	}
	
	
}
