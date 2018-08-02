package com.xh.service.menu;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.xh.common.CommonException;
import com.xh.dao.jsondatasource.MenuDao;
import com.xh.dto.MenuDto;
import com.xh.service.common.BaseService;
import com.xh.service.common.ServiceException;
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
	public void addMenu(MenuDto menu) throws CommonException {
		
		if( menu ==null || Utils.isEmpty(menu.getName()) ){
			throw new ServiceException(" menu cannot be empty !");
		}
		MenuDto oldMenu= null;
		try{
			oldMenu = getMenuByName(menu.getName());
		}catch(CommonException e){
		}
		if(oldMenu != null){
			throw new ServiceException(" menu "+oldMenu.getName()+" already exists !");
		}
		if( !Utils.isEmpty(menu.getRoute()) ){
			oldMenu= null;
			try{
				oldMenu = menuDao.getMenuByRoute(menu.getRoute()) ;
			}catch(CommonException e){
			}
			if(oldMenu != null){
				throw new ServiceException(" menu Route "+oldMenu.getRoute()+" already exists !");
			}
		}
		if(menu.isCreateTemplate() !=null && menu.isCreateTemplate()){
			if( Utils.isEmpty(menu.getRoute()) ){
				throw new ServiceException(" Create Template ,menu Route cannot be empty  !");
			}
			MenuCreateFile.createFile(menu);
		}
		
		menuDao.addMenu(menu);
	}

	@Override
	public MenuDto deleteMenuByKey(String key) throws CommonException {
		
		MenuDto oldMenu  = getMenuByKey(key);
		if (oldMenu.isAdmin()) {
			throw new ServiceException("  menu "+oldMenu.getName() +" is cannot be deleted ");
		}
		
		List<MenuDto> childList = getCascadeMenuByKey(key);
		for(MenuDto childMenu : childList){
			menuDao.deleteMenu(childMenu.getId());
		}
		return menuDao.deleteMenu(key);
	}

	@Override
	public MenuDto updateMenuByKey(String id ,MenuDto menu) throws CommonException {
		MenuDto oldMenu = menuDao.getMenuById(id);
		if(oldMenu.isAdmin()){
			throw new ServiceException(" the menu "+oldMenu.getName() +" cannot be modified !") ;
		}
		updateObjectToBean(menu, oldMenu);
		return menuDao.updateMenu(oldMenu);
	}

	@Override
	public MenuDto getMenuByKey(String key) throws CommonException {
		return menuDao.getMenuById(key);
	}
	
	
	private List<MenuDto> getCascadeMenuByKey(String key) throws CommonException {
		List<MenuDto> result = Utils.newArrayList();
		List<MenuDto> childList = menuDao.getMenuListByMpid(key);
		if(childList!=null && childList.size() >0){
			result.addAll(childList);
			for(MenuDto childMenu:childList){
				result.addAll(getCascadeMenuByKey(childMenu.getId()));
			} 
		}
		return result ;
		
	}
	
	
	public MenuDto getMenuByName(String name) throws CommonException {
		return menuDao.getMenuByName(name) ;
	}
	

	@Override
	public List<MenuDto> getMenuList() throws CommonException {
		return  menuDao.getMenuListBySort();
	}
	


}
