package com.xh.dao.jsondatasource;

import java.util.Comparator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.xh.common.CommonException;
import com.xh.dao.jsondatasource.jsonDatabase.ConditionFilter;
import com.xh.dao.jsondatasource.jsonDatabase.JsonDataSource;
import com.xh.dto.MenuDto;

@Component
public class MenuDao {
	
	@Autowired
	JsonDataSource datasource ;
	
	private final String tableName = "MenuDto" ;
	private final Class<MenuDto> t = MenuDto.class ;
	
	public MenuDto getMenuByName(String name) throws CommonException{
		MenuDto MenuDto = datasource.findRowByKey(tableName, new ConditionFilter<MenuDto>("name", name), t);
		if( MenuDto == null){
			throw new CommonException(" MenuDto:"+name +"  not exist!");
		}
		return MenuDto ;
	}
	
	public MenuDto getMenuByRoute(String route) throws CommonException{
		MenuDto MenuDto = datasource.findRowByKey(tableName, new ConditionFilter<MenuDto>("route", route), t);
		if( MenuDto == null){
			throw new CommonException(" MenuDto route:"+route +"  not exist!");
		}
		return MenuDto ;
	}
	
	public MenuDto getMenuById(String id) throws CommonException{
		MenuDto MenuDto = datasource.findRowByKey(tableName, new ConditionFilter<MenuDto>("id", id), t);
		if( MenuDto == null){
			throw new CommonException(" MenuDto:"+id +"  not exist!");
		}
		return MenuDto ;
	}
	
	public List<MenuDto> getMenuList() throws CommonException {
		return datasource.getListFromTable(tableName, t);
	}
	
	
	public List<MenuDto> getMenuListBySort() throws CommonException {
		
		Comparator<MenuDto> comparator = new Comparator<MenuDto>(){
			@Override
			public int compare(MenuDto o1, MenuDto o2) {
				return o1.getId().compareTo(o2.getId());
			}};
		return datasource.getListFromTableBySort(tableName, t, comparator);
	}
	
	public List<MenuDto> getMenuListByMpid(String mpid) throws CommonException{
		return datasource.findListRowByKey(tableName, new ConditionFilter<MenuDto>("mpid", mpid), t);
	}
	
	public void addMenu(MenuDto MenuDto) throws CommonException {
		datasource.addTableRow(tableName, MenuDto, t);
	}
	
	
	public MenuDto deleteMenu(String menuId) throws CommonException {
		return datasource.deleteByKey(tableName,  new ConditionFilter<MenuDto>("id", menuId), t);
	}
	
	public MenuDto updateMenu(MenuDto MenuDto) throws CommonException {
		return datasource.updateTableRow(tableName, MenuDto, new ConditionFilter<MenuDto>("id", MenuDto.getId()), t);
	}
	
	public int getMaxId() throws CommonException {
		return datasource.getMaxId(tableName);
	}
}
