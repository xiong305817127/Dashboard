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
import com.xh.dto.PermissionDto;
import com.xh.dto.UserDto;
import com.xh.service.permission.PermissionService;
import com.xh.util.Utils;
import com.xh.web.common.BaseController;

@Controller
@RequestMapping(Utils.API_PREFIX)
public class PermissionController extends BaseController{
	
	@Autowired
	PermissionService permissionService;
	
	@RequestMapping(method=RequestMethod.GET,value="/permissions")
	public @ResponseBody Object getPermissions(@SessionAttribute UserDto user ) throws  CommonException {
		return permissionService.getPermissions();
	}


	
	@RequestMapping(method=RequestMethod.POST,value="/permissions")
	public @ResponseBody Object addPermission(@RequestBody PermissionDto permission ) throws  CommonException {
		return  permissionService.addPermission(permission);
	}

	@RequestMapping(method=RequestMethod.PATCH, value="/permission/{role}")
	public @ResponseBody Object updatePermission( @PathVariable("role") String role,@RequestBody PermissionDto permission) throws  CommonException {
		return permissionService.updatePermissionByRole(role, permission);
	}

	@RequestMapping(method=RequestMethod.DELETE, value="/permission/{role}")
	public @ResponseBody Object deletePermission( @PathVariable("role") String role) throws  CommonException {
		return permissionService.deletePermissionByRole(role);
	}
	
}
