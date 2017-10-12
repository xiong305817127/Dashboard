package com.xh.service.login;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.xh.common.service.BaseService;
import com.xh.dto.UserDto;
import com.xh.entry.User;
import com.xh.service.user.UserService;

/**
 * 网站配置
 * 
 * @author Zihan
 * 
 */
@Service
public class LoginServiceImpl extends BaseService implements LoginService{

	@Resource
	private UserService userService;

	@Override
	public UserDto login(UserDto userOld) throws Exception {
		
		 User userNew = userService.getUserByUserName(userOld.getUsername());
		 if(userOld.getPassword().equals(userNew.getPassword())){
			 return (UserDto)updateObjectToBean(userNew, userOld);
		 }
		 return null ;
		
	}

	@Override
	public void logout() throws Exception {
		
	}

}
