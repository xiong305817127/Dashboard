package com.xh.dao.jsondatasource;

import java.util.Comparator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.xh.common.CommonException;
import com.xh.dao.jsondatasource.jsonDatabase.ConditionFilter;
import com.xh.dao.jsondatasource.jsonDatabase.JsonDataSource;
import com.xh.dto.UserDto;
import com.xh.util.Utils;

@Component
public class UserDao {
	
	@Autowired
	JsonDataSource datasource ;
	@Autowired
	PermissionDao permissionDao;
	
	private final String tableName = "Users" ;
	private final Class<UserDto> t = UserDto.class ;
	
	public UserDto getUserById(String id) throws CommonException{
		UserDto UserDto = datasource.findRowByKey(tableName, new ConditionFilter<UserDto>("id",id), t);
		if( UserDto == null){
			throw new CommonException(" UserDto :"+id +"  not exist!");
		}
		UserDto.setPermissions(permissionDao.getPermissionByRole(UserDto.getPermissionId()));
		return UserDto ;
	}
	
	public UserDto getUserByUserName(String userName) throws CommonException{
		UserDto UserDto = datasource.findRowByKey(tableName, new ConditionFilter<UserDto>("username",userName), t);
		if( UserDto == null){
			throw new CommonException(" UserDto :"+userName +"  not exist!");
		}
		UserDto.setPermissions(permissionDao.getPermissionByRole(UserDto.getPermissionId()));
		return UserDto ;
	}
	
	public List<UserDto> getUserList() throws CommonException {
		List<UserDto> userList = datasource.getListFromTable(tableName, t);
		return Utils.transListToList(userList, new Utils.DtoTransData<UserDto>() {
			@Override
			public UserDto dealData(Object obj, int index) throws CommonException {
				UserDto UserDto = (UserDto)obj;
				UserDto.setPermissions(permissionDao.getPermissionByRole(UserDto.getPermissionId()));
				return UserDto;
			}
		});
		
	}
	
	public List<UserDto> getUserListByPermission(String role) throws CommonException{
		List<UserDto> userList = datasource.findListRowByKey(tableName, new ConditionFilter<UserDto>("permissionId",role), t);
		return Utils.transListToList(userList, new Utils.DtoTransData<UserDto>() {
			@Override
			public UserDto dealData(Object obj, int index) throws CommonException {
				UserDto UserDto = (UserDto)obj;
				UserDto.setPermissions(permissionDao.getPermissionByRole(UserDto.getPermissionId()));
				return UserDto;
			}
		});
	}
	
	public List<UserDto> getUserListBySort() throws CommonException {
		Comparator<UserDto> comparator = new Comparator<UserDto>(){
			@Override
			public int compare(UserDto o1, UserDto o2) {
				return o1.getId().compareTo(o2.getId());
			}};
		List<UserDto> userList = datasource.getListFromTableBySort(tableName, t,comparator);
		return Utils.transListToList(userList, new Utils.DtoTransData<UserDto>() {
			@Override
			public UserDto dealData(Object obj, int index) throws CommonException {
				UserDto UserDto = (UserDto)obj;
				UserDto.setPermissions(permissionDao.getPermissionByRole(UserDto.getPermissionId()));
				return UserDto;
			}
		});
		
	}
	
	public int getMaxId() throws CommonException {
		return datasource.getMaxId(tableName);
	}
	
	public void addUser(UserDto UserDto) throws CommonException {
		datasource.addTableRow(tableName, UserDto, t);
	}
	
	
	public UserDto deleteUser(String userId) throws CommonException {
		return datasource.deleteByKey(tableName, new ConditionFilter<UserDto>("id",userId), t);
	}
	
	public UserDto updateUser(UserDto UserDto) throws CommonException {
		return datasource.updateTableRow(tableName, UserDto, new ConditionFilter<UserDto>("username", UserDto.getUsername()), t);
	}
	
}
