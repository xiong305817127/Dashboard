package com.xh.dao.jsondatasource;

import java.util.Comparator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.xh.common.exception.WebException;
import com.xh.dao.jsondatasource.jsonDatabase.ConditionFilter;
import com.xh.dao.jsondatasource.jsonDatabase.JsonDataSource;
import com.xh.entry.User;
import com.xh.util.Utils;

@Component
public class UserDao {
	
	@Autowired
	JsonDataSource datasource ;
	@Autowired
	PermissionDao permissionDao;
	
	private final String tableName = "Users" ;
	private final Class<User> t = User.class ;
	
	public User getUserById(String id) throws Exception{
		User user = datasource.findRowByKey(tableName, new ConditionFilter<User>("id",id), t);
		if( user == null){
			throw new WebException(" user :"+id +"  not exist!");
		}
		user.setPermissions(permissionDao.getPermissionByRole(user.getPermissionId()));
		return user ;
	}
	
	public User getUserByUserName(String userName) throws Exception{
		User user = datasource.findRowByKey(tableName, new ConditionFilter<User>("username",userName), t);
		if( user == null){
			throw new WebException(" user :"+userName +"  not exist!");
		}
		user.setPermissions(permissionDao.getPermissionByRole(user.getPermissionId()));
		return user ;
	}
	
	public List<User> getUserList() throws Exception {
		List<User> userList = datasource.getListFromTable(tableName, t);
		return Utils.transListToList(userList, new Utils.DtoTransData<User>() {
			@Override
			public User dealData(Object obj, int index) throws Exception {
				User user = (User)obj;
				user.setPermissions(permissionDao.getPermissionByRole(user.getPermissionId()));
				return user;
			}
		});
		
	}
	
	public List<User> getUserListByPermission(String role) throws Exception{
		List<User> userList = datasource.findListRowByKey(tableName, new ConditionFilter<User>("permissionId",role), t);
		return Utils.transListToList(userList, new Utils.DtoTransData<User>() {
			@Override
			public User dealData(Object obj, int index) throws Exception {
				User user = (User)obj;
				user.setPermissions(permissionDao.getPermissionByRole(user.getPermissionId()));
				return user;
			}
		});
	}
	
	public List<User> getUserListBySort() throws Exception {
		Comparator<User> comparator = new Comparator<User>(){
			@Override
			public int compare(User o1, User o2) {
				return o1.getId().compareTo(o2.getId());
			}};
		List<User> userList = datasource.getListFromTableBySort(tableName, t,comparator);
		return Utils.transListToList(userList, new Utils.DtoTransData<User>() {
			@Override
			public User dealData(Object obj, int index) throws Exception {
				User user = (User)obj;
				user.setPermissions(permissionDao.getPermissionByRole(user.getPermissionId()));
				return user;
			}
		});
		
	}
	
	public int getMaxId() throws Exception {
		return datasource.getMaxId(tableName);
	}
	
	public void addUser(User user) throws Exception {
		datasource.addTableRow(tableName, user, t);
	}
	
	
	public User deleteUser(String userId) throws Exception {
		return datasource.deleteByKey(tableName, new ConditionFilter<User>("id",userId), t);
	}
	
	public User updateUser(User user) throws Exception {
		return datasource.updateTableRow(tableName, user, new ConditionFilter<User>("username", user.getUsername()), t);
	}
	
}
