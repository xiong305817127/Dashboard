package com.xh.dao.jsondatasource;

import java.util.Comparator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.xh.common.exception.WebException;
import com.xh.dao.jsondatasource.jsonDatabase.JsonDataSource;
import com.xh.entry.Menu;

@Component
public class MenuDao {
	
	@Autowired
	JsonDataSource datasource ;
	
	private final String tableName = "Menu" ;
	private final Class<Menu> t = Menu.class ;
	private final JsonDataSource.KeyFilter<Menu> idFilter = new JsonDataSource.KeyFilter<Menu>(){

		@Override
		public boolean findByKey(Menu m,Object key) {
			return m != null &&isEqual(m.getId(), key);
		}
		
	};
	
	private final JsonDataSource.KeyFilter<Menu> nameFilter = new JsonDataSource.KeyFilter<Menu>(){

		@Override
		public boolean findByKey(Menu m,Object key) {
			return m != null &&isEqual(m.getName(), key);
		}
		
	};
	
	private final JsonDataSource.KeyFilter<Menu> routeFilter = new JsonDataSource.KeyFilter<Menu>(){

		@Override
		public boolean findByKey(Menu m,Object key) {
			return m != null &&isEqual(m.getRoute(), key);
		}
		
	};
	
	private final JsonDataSource.KeyFilter<Menu> mpidFilter = new JsonDataSource.KeyFilter<Menu>(){

		@Override
		public boolean findByKey(Menu m,Object key) {
			return m != null &&isEqual(m.getMpid(), key);
		}
		
	};
	
	public Menu getMenuByName(String name) throws Exception{
		Menu menu = datasource.findRowByKey(tableName, name, nameFilter, t);
		if( menu == null){
			throw new WebException(" menu:"+name +"  not exist!");
		}
		return menu ;
	}
	
	public Menu getMenuByRoute(String route) throws Exception{
		Menu menu = datasource.findRowByKey(tableName, route, routeFilter, t);
		if( menu == null){
			throw new WebException(" menu route:"+route +"  not exist!");
		}
		return menu ;
	}
	
	public Menu getMenuById(String id) throws Exception{
		Menu menu = datasource.findRowByKey(tableName, id, idFilter, t);
		if( menu == null){
			throw new WebException(" menu:"+id +"  not exist!");
		}
		return menu ;
	}
	
	public List<Menu> getMenuList() throws Exception {
		return datasource.getListFromTable(tableName, t);
	}
	
	
	public List<Menu> getMenuListBySort() throws Exception {
		
		Comparator<Menu> comparator = new Comparator<Menu>(){
			@Override
			public int compare(Menu o1, Menu o2) {
				return o1.getId().compareTo(o2.getId());
			}};
		return datasource.getListFromTableBySort(tableName, t, comparator);
	}
	
	public List<Menu> getMenuListByMpid(String mpid) throws Exception{
		return datasource.findListRowByKey(tableName, mpid, mpidFilter, t);
	}
	
	public void addMenu(Menu menu) throws Exception {
		datasource.addTableRow(tableName, menu, t);
	}
	
	
	public Menu deleteMenu(String menuId) throws Exception {
		return datasource.deleteByKey(tableName, menuId, idFilter, t);
	}
	
	public Menu updateMenu(Menu menu) throws Exception {
		return datasource.updateTableRow(tableName, menu, menu.getId(), idFilter, t);
	}
	
	public int getMaxId() throws Exception {
		return datasource.getMaxId(tableName);
	}
}
