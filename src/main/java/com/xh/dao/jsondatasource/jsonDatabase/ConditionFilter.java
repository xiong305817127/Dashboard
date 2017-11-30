package com.xh.dao.jsondatasource.jsonDatabase;

import java.util.Arrays;
import java.util.List;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import com.google.common.collect.Lists;
import com.xh.util.ReflectUtils;
import com.xh.util.Utils;


/**
 * 利用类似  key1=='12321'&&key2=='3456'||key3#('abc')# 脚本获取逻辑结果<br>
 * 等于  : key1=='12321'<br>
 * 包含: key1#('12321')#<br>
 * 逻辑组合: 与: && ,或: ||<br>
 * @author XH<br>
 * @since 2017年11月30日
 *
 * @param <T>
 */
 public class ConditionFilter<T> {
	
	private String script;
	private List<String> fields;
	
	/**
	 * 简单的是否相等 条件对象
	 * @param field
	 * @param key
	 */
	public ConditionFilter(String field, Object  key) {
		super();
		this.script = field+"=='"+key+"'";
		this.fields=Lists.newArrayList(field);
	}
	
	public ConditionFilter(String script , String... fields ) {
		super();
		this.fields = Arrays.asList(fields);
		if(script.contains("#(")) {
			script.replaceAll("#(", ".indexOf(");
			script.replaceAll(")#", ")!=-1");
		}
		this.script = script;
	}
	
	/**
	 * @param fieldskeys : [ id ,123,&& ,key1,aaaa,||,key2#,bbbb ]
	 *  效果 : id=='123'&&key1=='aaaa'||key2.indexOf('bbbb')!=-1
	 */
	public ConditionFilter(Object...  fieldskeys) {
		super();
		if(fieldskeys.length>=2) {
			fields = Lists.newArrayList();
			StringBuffer scriptBuf = new StringBuffer() ;
			
			boolean isField = true ;
			String opt = "==" ;
			for(int i=0;i<fieldskeys.length;i++){
				String val = fieldskeys[i] != null ? fieldskeys[i].toString() :"" ;
				if(isField) {
					if(val.endsWith("#")) {
						val = val.toString().substring(0,val.length()-2);
						opt = "#";
					}else {
						opt="==";
					}
					fields.add(val);
					scriptBuf.append(val);
					isField=false ;
				}else if ("&&".equals(val) || "||".equals(val) ){
					scriptBuf.append(val);
					isField=true ;
				}else {
					if("#".equals(opt)) {
						scriptBuf.append(".indexOf('"+val+"')!=-1");
					}else {
						scriptBuf.append("=='"+val+"'");
					}
					opt="==";
				}

			}
			this.script = scriptBuf.toString();
		}
	}

	public boolean findByCondition(T t) {
		
		if(!Utils.isEmpty(script)) {
			 try {
				ScriptEngineManager manager = new ScriptEngineManager();
				 ScriptEngine engine = manager.getEngineByName("js");
				 if(fields != null && fields.size() >0) {
					for(int i=0;i<fields.size();i++) {
						String field = fields.get(i);
						Object val = ReflectUtils.getOsgiField(t, field, true);
						engine.put(field,val);   
					}
				}
				Object result = engine.eval(script);
				return Boolean.valueOf(result.toString());
			} catch (ScriptException e) {
				e.printStackTrace();
				return false ;
			}
		}
		return true;
	}
	
}
