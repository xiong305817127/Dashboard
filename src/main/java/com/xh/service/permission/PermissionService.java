package com.xh.service.permission;

import java.util.List;

import com.xh.common.CommonException;
import com.xh.dto.PermissionDto;


public interface PermissionService {

	public PermissionDto addPermission( PermissionDto permission) throws CommonException;

	public PermissionDto deletePermissionByRole(String role) throws CommonException;
	
	public PermissionDto updatePermissionByRole(String role , PermissionDto permission) throws CommonException;

	public List<PermissionDto> getPermissions() throws CommonException;
	
	public PermissionDto getPermissionByRole(String role) throws CommonException;
	
}
