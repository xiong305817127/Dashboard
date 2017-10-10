package com.xh.service.menu;

import java.util.List;

import com.xh.dto.MenuDto;
import com.xh.entry.Menu;


public interface MenuService {

	public void addMenu(MenuDto menu) throws Exception;

	public Menu deleteMenuByKey(String key) throws Exception;

	public Menu updateMenuByKey(String id ,Menu menu) throws Exception;

	public Menu getMenuByKey(String key) throws Exception;

	public List<Menu> getMenuList() throws Exception;
	
}
