package com.xh.dao.jsondatasource;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.xh.common.exception.WebException;
import com.xh.dao.jsondatasource.jsonDatabase.ConditionFilter;
import com.xh.dao.jsondatasource.jsonDatabase.JsonDataSource;
import com.xh.entry.Permission;

@Component
public class PermissionDao {

	@Autowired
	JsonDataSource datasource;

	private final String tableName = "Permission";
	private final Class<Permission> t = Permission.class;

	public Permission getPermissionByRole(String role) throws Exception {
		
		Permission permission = datasource.findRowByKey(tableName,  new ConditionFilter<Permission>("role", role), t);
		if( permission == null){
			throw new WebException(" permission :"+role +"  not exist!");
		}
		return permission ;
	}

	public List<Permission> getPermissionList() throws Exception {
		return datasource.getListFromTable(tableName, t);
	}

	public void addPermission(Permission permission) throws Exception {
		datasource.addTableRow(tableName, permission, t);
	}

	public Permission deletePermission(String role) throws Exception {
		return datasource.deleteByKey(tableName,  new ConditionFilter<Permission>("role", role), t);
	}

	public Permission updatePermission(Permission permission) throws Exception {
		return datasource.updateTableRow(tableName, permission, new ConditionFilter<Permission>("role",  permission.getRole()), t);
	}

}
