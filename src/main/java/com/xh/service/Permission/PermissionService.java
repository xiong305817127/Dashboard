package com.xh.service.Permission;

import java.util.List;

import com.xh.entry.Permission;

public interface PermissionService {

	public Permission addPermission( Permission permission) throws Exception;

	public Permission deletePermissionByRole(String role) throws Exception;
	
	public Permission updatePermissionByRole(String role , Permission permission) throws Exception;

	public List<Permission> getPermissions() throws Exception;
	
	public Permission getPermissionByRole(String role) throws Exception;
	
}
