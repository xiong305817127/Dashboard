package com.xh.service.login;

import com.xh.common.CommonException;
import com.xh.dto.UserDto;

public interface LoginService {

	public UserDto login(UserDto user) throws CommonException;

	public void logout() throws CommonException;


}
