package com.xh.service.Permission;

import java.util.Date;
import java.util.List;
import java.util.Random;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.xh.common.dto.PaginationDto;
import com.xh.common.exception.WebException;
import com.xh.common.service.BaseService;
import com.xh.dao.jsondatasource.PermissionDao;
import com.xh.dao.jsondatasource.UserDao;
import com.xh.entry.Permission;
import com.xh.entry.User;
import com.xh.util.Utils;

import net.sourceforge.pinyin4j.PinyinHelper;

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

	public List<Permission> getPermissions() throws Exception {
		return permissionDao.getPermissionList();
	}

	@Override
	public Permission addPermission(Permission permission) throws Exception {
		if(permission ==null || Utils.isEmpty(permission.getRole())){
			throw new WebException(" permission role cannot be empty !");
		}
		Permission oldPermission = null;
		try{
			oldPermission = getPermissionByRole(permission.getRole());
		}catch(Exception e){
		}
		if(oldPermission != null){
			throw new WebException(" Permission "+permission.getRole()+" already exists !");
		}
		
		permission.setId( permission.getRole().toLowerCase() );
		permissionDao.addPermission(permission);
		return permission;
		
	}

	@Override
	public Permission deletePermissionByRole(String role) throws Exception {
		Permission oldPermission = getPermissionByRole(role);
		if ("admin".equals(oldPermission.getRole()) || "guest".equals(oldPermission.getRole()) ) {
			throw new WebException(" Permission "+oldPermission.getRole()+" is cannot be deleted ");
		}
		
		List<User> oldUserList = userDao.getUserListByPermission(oldPermission.getRole());
		for(User user : oldUserList){
			user.setPermissionId("guest");
			user.setPermissions(getPermissionByRole("guest"));
			userDao.updateUser(user);
		}
		
		return permissionDao.deletePermission(role);
	}

	@Override
	public Permission updatePermissionByRole(String role, Permission permission) throws Exception {
		Permission oldPermission = getPermissionByRole(role);
		if ("admin".equals(oldPermission.getRole())) {
			throw new WebException(" the Permission "+role +" cannot be modified !") ;
		}
		oldPermission.setVisit(permission.getVisit());
		return permissionDao.updatePermission(oldPermission);
	}
	
	
	@Override
	public Permission getPermissionByRole(String role) throws Exception {
		return permissionDao.getPermissionByRole(role);
	}

}
