package com.xh.service.login;

import com.xh.entry.User;

public interface LoginService {

	public User login(User user) throws Exception;

	public void logout() throws Exception;


}
