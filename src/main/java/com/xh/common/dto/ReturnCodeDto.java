/**
 * 云化数据集成系统 
 * iDatrix CloudETL
 */
package com.xh.common.dto;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.util.JSONUtils;

/**
 * http返回结果dto
 * @author JW
 * @since 05-12-2017
 *
 */
public class ReturnCodeDto {
	
	private int code;
    private String message;
    private Object data;
    
    /**
	 * @return the code
	 */
	public int getCode() {
		return code;
	}
	/**
	 * @param  设置 code
	 */
	public void setCode(int code) {
		this.code = code;
	}
	public void setMessage(String message) {
        this.message = message;
    }
    public String getMessage() {
        return message;
    }
    
    /**
	 * @return the data
	 */
	public Object getData() {
		return data;
	}
	/**
	 * @param  设置 data
	 */
	public void setData(Object data) {
		this.data = data;
	}
	/**
	 * Constructor.
	 */
	public ReturnCodeDto() {
		
	}
	
	/**
	 * @param retCode
	 * @param message
	 */
	public ReturnCodeDto(int retCode, String message) {
		this.code = retCode;
		this.message = message;
	}
	
	/**
	 * @param retCode
	 * @param message
	 */
	public ReturnCodeDto(int retCode,Object obj) {
		this.code = retCode;
		this.data=obj;
	}
	
	/*
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		if(code == 0 && data != null){
			if(JSONUtils.isArray(data)){
				return "ReturnCodeDto [retCode=" + code + ", message=" + message + ", data="+JSONArray.fromObject(data).toString()+"]";
			}else if( !JSONUtils.isObject(data)){
				return "ReturnCodeDto [retCode=" + code + ", message=" + message + ", data="+data.toString()+"]";
			}else if( JSONUtils.isObject(data) && data instanceof CommonDto){
				return "ReturnCodeDto [retCode=" + code + ", message=" + message + ", data="+JSONObject.fromObject(data).toString()+"]";
			}
			return "ReturnCodeDto [retCode=" + code + ", message=" + message + ", data="+data+"]";
		}
		return "ReturnCodeDto [retCode=" + code + ", message=" + message + "]";
	}

}
