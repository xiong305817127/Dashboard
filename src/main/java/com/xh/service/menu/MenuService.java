package com.xh.service.menu;

import java.util.List;

import com.xh.common.CommonException;
import com.xh.dto.MenuDto;


public interface MenuService {

	public void addMenu(MenuDto menu) throws CommonException;

	public MenuDto deleteMenuByKey(String key) throws CommonException;

	public MenuDto updateMenuByKey(String id ,MenuDto menu) throws CommonException;

	public MenuDto getMenuByKey(String key) throws CommonException;

	public List<MenuDto> getMenuList() throws CommonException;
	
}
