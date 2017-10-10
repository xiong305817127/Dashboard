package com.xh.service.menu;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.xh.common.exception.WebException;
import com.xh.common.service.BaseService;
import com.xh.dao.jsondatasource.MenuDao;
import com.xh.dto.MenuDto;
import com.xh.entry.Menu;
import com.xh.util.Utils;

/**
 * 网站配置
 * 
 * @author Zihan
 * 
 */
@Service
public class MenuServiceImpl extends BaseService implements MenuService{

	@Resource
	private MenuDao menuDao;

	@Override
	public void addMenu(MenuDto menu) throws Exception {
		
		if( menu ==null || Utils.isEmpty(menu.getName()) ){
			throw new WebException(" menu cannot be empty !");
		}
		Menu oldMenu= null;
		try{
			oldMenu = getMenuByName(menu.getName());
		}catch(Exception e){
		}
		if(oldMenu != null){
			throw new WebException(" menu "+oldMenu.getName()+" already exists !");
		}
		if( !Utils.isEmpty(menu.getRoute()) ){
			oldMenu= null;
			try{
				oldMenu = menuDao.getMenuByRoute(menu.getRoute()) ;
			}catch(Exception e){
			}
			if(oldMenu != null){
				throw new WebException(" menu Route "+oldMenu.getRoute()+" already exists !");
			}
		}
		if(menu.isCreateTemplate() !=null && menu.isCreateTemplate()){
			if( Utils.isEmpty(menu.getRoute()) ){
				throw new WebException(" Create Template ,menu Route cannot be empty  !");
			}
			MenuCreateFile.createFile(menu);
		}
		
		menuDao.addMenu(menu);
	}

	@Override
	public Menu deleteMenuByKey(String key) throws Exception {
		
		Menu oldMenu  = getMenuByKey(key);
		if (oldMenu.isAdmin()) {
			throw new WebException("  menu "+oldMenu.getName() +" is cannot be deleted ");
		}
		
		List<Menu> childList = getCascadeMenuByKey(key);
		for(Menu childMenu : childList){
			menuDao.deleteMenu(childMenu.getId());
		}
		return menuDao.deleteMenu(key);
	}

	@Override
	public Menu updateMenuByKey(String id ,Menu menu) throws Exception {
		Menu oldMenu = menuDao.getMenuById(id);
		if(oldMenu.isAdmin()){
			throw new WebException(" the menu "+oldMenu.getName() +" cannot be modified !") ;
		}
		updateObjectToBean(menu, oldMenu);
		return menuDao.updateMenu(oldMenu);
	}

	@Override
	public Menu getMenuByKey(String key) throws Exception {
		return menuDao.getMenuById(key);
	}
	
	
	private List<Menu> getCascadeMenuByKey(String key) throws Exception {
		List<Menu> result = Utils.newArrayList();
		List<Menu> childList = menuDao.getMenuListByMpid(key);
		if(childList!=null && childList.size() >0){
			result.addAll(childList);
			for(Menu childMenu:childList){
				result.addAll(getCascadeMenuByKey(childMenu.getId()));
			} 
		}
		return result ;
		
	}
	
	
	public Menu getMenuByName(String name) throws Exception {
		return menuDao.getMenuByName(name) ;
	}
	

	@Override
	public List<Menu> getMenuList() throws Exception {
		return  menuDao.getMenuListBySort();
	}
	


}
