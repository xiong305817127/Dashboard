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
import com.xh.entry.Permission;
import com.xh.entry.User;
import com.xh.service.Permission.PermissionService;
import com.xh.util.Utils;

@Controller
@RequestMapping(Utils.API_PREFIX)
public class PermissionController extends BaseController{
	
	@Autowired
	PermissionService permissionService;
	
	@RequestMapping(method=RequestMethod.GET,value="/permissions")
	public @ResponseBody Object getPermissions(@SessionAttribute User user ) throws Exception {
		return permissionService.getPermissions();
	}


	
	@RequestMapping(method=RequestMethod.POST,value="/permissions")
	public @ResponseBody Object addPermission(@RequestBody Permission permission ) throws Exception {
		return  permissionService.addPermission(permission);
	}

	@RequestMapping(method=RequestMethod.PATCH, value="/permission/{role}")
	public @ResponseBody Object updatePermission( @PathVariable("role") String role,@RequestBody Permission permission) throws Exception {
		return permissionService.updatePermissionByRole(role, permission);
	}

	@RequestMapping(method=RequestMethod.DELETE, value="/permission/{role}")
	public @ResponseBody Object deletePermission( @PathVariable("role") String role) throws Exception {
		return permissionService.deletePermissionByRole(role);
	}
	
}
