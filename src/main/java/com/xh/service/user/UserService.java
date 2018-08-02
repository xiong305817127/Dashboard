package com.xh.service.user;

import java.util.List;

import com.xh.common.CommonException;
import com.xh.dto.UserDto;
import com.xh.dto.common.PaginationDto;

public interface UserService {

	public UserDto addUser(UserDto user) throws CommonException;

	public UserDto deleteUserById(String key) throws CommonException;
	
	public void deleteUserByIds(List<String> id) throws CommonException ;

	public UserDto updateUserById(String id ,UserDto user) throws CommonException;

	public UserDto getUserById(String id) throws CommonException;
	
	public UserDto getUserByUserName(String UserName) throws CommonException;

	public List<UserDto> getUserList() throws CommonException;
	
	public PaginationDto<UserDto> getUserList(Integer page,Integer pageSize,String search) throws CommonException;

}
