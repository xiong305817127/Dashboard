package com.xh.dao.jsondatasource;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.xh.common.CommonException;
import com.xh.dao.jsondatasource.jsonDatabase.ConditionFilter;
import com.xh.dao.jsondatasource.jsonDatabase.JsonDataSource;
import com.xh.dto.PermissionDto;

@Component
public class PermissionDao {

	@Autowired
	JsonDataSource datasource;

	private final String tableName = "PermissionDto";
	private final Class<PermissionDto> t = PermissionDto.class;

	public PermissionDto getPermissionByRole(String role) throws CommonException {
		
		PermissionDto PermissionDto = datasource.findRowByKey(tableName,  new ConditionFilter<PermissionDto>("role", role), t);
		if( PermissionDto == null){
			throw new CommonException(" PermissionDto :"+role +"  not exist!");
		}
		return PermissionDto ;
	}

	public List<PermissionDto> getPermissionList() throws CommonException {
		return datasource.getListFromTable(tableName, t);
	}

	public void addPermission(PermissionDto PermissionDto) throws CommonException {
		datasource.addTableRow(tableName, PermissionDto, t);
	}

	public PermissionDto deletePermission(String role) throws CommonException {
		return datasource.deleteByKey(tableName,  new ConditionFilter<PermissionDto>("role", role), t);
	}

	public PermissionDto updatePermission(PermissionDto PermissionDto) throws CommonException {
		return datasource.updateTableRow(tableName, PermissionDto, new ConditionFilter<PermissionDto>("role",  PermissionDto.getRole()), t);
	}

}
