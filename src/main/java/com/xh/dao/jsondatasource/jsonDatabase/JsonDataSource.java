package com.xh.dao.jsondatasource.jsonDatabase;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.xh.util.Utils;
import com.xh.vfs.WebVFS;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

@Component
public class JsonDataSource {
	
	@SuppressWarnings("unchecked")
	public <T> List<T> getListFromTable(String tableName, Class<T> t) throws Exception{
		
		JSONArray jsonList = getTableFile(tableName);
		return (List<T>) JSONArray.toCollection(jsonList, t);
		
	}
	
	
	public <T> List<T> getListFromTableBySort(String tableName, Class<T> t,Comparator<? super T> comparator) throws Exception{
		List<T> list = getListFromTable(tableName, t);
		return list.stream().sorted(comparator).collect(Collectors.toList());
	}
	
	@SuppressWarnings("unchecked")
	public <T> int getMaxId(String tableName) throws Exception{
		
		JSONArray jsonList = getTableFile(tableName);
		int maxId = 0;
		for(int i=0;i<jsonList.size();i++){
			JSONObject obj = jsonList.getJSONObject(i);
			Integer idInt = null ;
			if(obj.containsKey("id")){
				try {
					idInt = Integer.parseInt(obj.get("id").toString()) ;
				} catch (Exception e) {
				}
			}
			
			if( idInt == null ){
				return maxId;
			}
			if( idInt > maxId ){
				maxId = idInt ;
			}
		}
		
		return maxId ;
		
	}
	
	public <T> T findRowByKey(String tableName,Object key ,  KeyFilter<T> filter , Class<T> t) throws Exception{
		
		List<T> list = getListFromTable(tableName, t);
		Optional<T> opt = list.stream().filter( tt -> { return filter.findByKey(tt,key);}).findFirst();
		if( opt.isPresent()){
			return opt.get();
		}
		return null ;
	}
	
	
	public <T> List<T> findListRowByKey(String tableName,Object key ,  KeyFilter<T> filter , Class<T> t) throws Exception{
		
		List<T> list = getListFromTable(tableName, t);
		return list.stream().filter( tt -> { return filter.findByKey(tt,key);}).collect(Collectors.toList());
	}
	
	public <T> T updateTableRow(String tableName, T tt,Object key , KeyFilter<T> filter, Class<T> t) throws Exception{
		T old = deleteByKey(tableName,key, filter, t);
		addTableRow(tableName, tt, t);
		return old ;
	}
	
	public <T> void addTableRow(String tableName, T tt, Class<T> t) throws Exception{
		
		List<T> list = getListFromTable(tableName, t);
		list.add(tt);
		saveTableFile(tableName, list);
		
	}
	
	public <T> T deleteByKey(String tableName, Object key ,KeyFilter<T> filter, Class<T> t) throws Exception{
		
		List<T> list = getListFromTable(tableName, t);
		List<T> needToDel = list.stream().filter( tt -> { return filter.findByKey(tt,key);}).collect(Collectors.toList());
		for(T tt: needToDel){
			list.remove(tt);
		}
		saveTableFile(tableName, list);
		
		return ( list !=null && list.size() >0 ) ?list.get(0) : null ;
		
	}
	
	private   JSONArray  getTableFile(String tableName) throws  Exception{
		
		String fileName = JsonDataSource.class.getClassLoader().getResource("").getPath()+"/"+JsonDataSource.class.getPackage().getName().replaceAll("\\.", "/")+"/"+tableName+".json";
		if( !WebVFS.fileExists(fileName)){
			WebVFS.createFile(fileName);
		}
		
		String jsonStr = WebVFS.getTextFileContent(fileName, "UTF-8");
		if(Utils.isEmpty(jsonStr)){
			return new JSONArray() ;
		}
		return JSONArray.fromObject(jsonStr);
	}
	
	private <T>  void  saveTableFile(String tableName,List<T> list) throws  Exception{
		
		String fileName = JsonDataSource.class.getClassLoader().getResource("").getPath()+"/"+JsonDataSource.class.getPackage().getName().replaceAll("\\.", "/")+"/"+tableName+".json";
		if( !WebVFS.fileExists(fileName)){
			WebVFS.createFile(fileName);
		}
		WebVFS.writerTextFileContent(fileName, JSONArray.fromObject(list).toString(), "UTF-8");
	}
	
	public interface KeyFilter<T>{
		
		boolean findByKey( T t ,Object key);
		
		default boolean isEqual(Object obj,Object key){
			return obj!=null && obj.equals(key) ;
		}
	}
}