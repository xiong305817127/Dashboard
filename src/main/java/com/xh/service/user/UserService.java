package com.xh.service.user;

import java.util.List;

import com.xh.common.dto.PaginationDto;
import com.xh.entry.User;

public interface UserService {

	public User addUser(User user) throws Exception;

	public User deleteUserById(String key) throws Exception;
	
	public void deleteUserByIds(List<String> id) throws Exception ;

	public User updateUserById(String id ,User user) throws Exception;

	public User getUserById(String id) throws Exception;
	
	public User getUserByUserName(String UserName) throws Exception;

	public List<User> getUserList() throws Exception;
	
	public PaginationDto<User> getUserList(Integer page,Integer pageSize,String search) throws Exception;

}
