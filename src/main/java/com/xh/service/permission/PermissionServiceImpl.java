package com.xh.service.permission;

import java.util.List;
import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.xh.common.CommonException;
import com.xh.dao.jsondatasource.PermissionDao;
import com.xh.dao.jsondatasource.UserDao;
import com.xh.dto.PermissionDto;
import com.xh.dto.UserDto;
import com.xh.service.common.BaseService;
import com.xh.service.common.ServiceException;
import com.xh.util.Utils;

/**
 * 网站配置
 * 
 * @author Zihan
 * 
 */
@Service
public class PermissionServiceImpl extends BaseService implements PermissionService {

	@Resource
	private UserDao userDao;
	@Resource
	private PermissionDao permissionDao;

	public List<PermissionDto> getPermissions() throws CommonException {
		return permissionDao.getPermissionList();
	}

	@Override
	public PermissionDto addPermission(PermissionDto permission) throws CommonException {
		if(permission ==null || Utils.isEmpty(permission.getRole())){
			throw new ServiceException(" permission role cannot be empty !");
		}
		PermissionDto oldPermission = null;
		try{
			oldPermission = getPermissionByRole(permission.getRole());
		}catch(CommonException e){
		}
		if(oldPermission != null){
			throw new ServiceException(" PermissionDto "+permission.getRole()+" already exists !");
		}
		
		permission.setId( permission.getRole().toLowerCase() );
		permissionDao.addPermission(permission);
		return permission;
		
	}

	@Override
	public PermissionDto deletePermissionByRole(String role) throws CommonException {
		PermissionDto oldPermission = getPermissionByRole(role);
		if ("admin".equals(oldPermission.getRole()) || "guest".equals(oldPermission.getRole()) ) {
			throw new ServiceException(" PermissionDto "+oldPermission.getRole()+" is cannot be deleted ");
		}
		
		List<UserDto> oldUserList = userDao.getUserListByPermission(oldPermission.getRole());
		for(UserDto user : oldUserList){
			user.setPermissionId("guest");
			user.setPermissions(getPermissionByRole("guest"));
			userDao.updateUser(user);
		}
		
		return permissionDao.deletePermission(role);
	}

	@Override
	public PermissionDto updatePermissionByRole(String role, PermissionDto permission) throws CommonException {
		PermissionDto oldPermission = getPermissionByRole(role);
		if ("admin".equals(oldPermission.getRole())) {
			throw new ServiceException(" the PermissionDto "+role +" cannot be modified !") ;
		}
		oldPermission.setVisit(permission.getVisit());
		return permissionDao.updatePermission(oldPermission);
	}
	
	
	@Override
	public PermissionDto getPermissionByRole(String role) throws CommonException {
		return permissionDao.getPermissionByRole(role);
	}

}
