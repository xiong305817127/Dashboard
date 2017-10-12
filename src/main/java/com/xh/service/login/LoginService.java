package com.xh.service.login;

import com.xh.dto.UserDto;

public interface LoginService {

	public UserDto login(UserDto user) throws Exception;

	public void logout() throws Exception;


}
