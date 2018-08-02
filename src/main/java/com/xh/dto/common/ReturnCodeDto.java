/**
 * 云化数据集成系统 
 * iDatrix CloudETL
 */
package com.xh.dto.common;

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
	
	private boolean success = true;
    private String message;
    private int statusCode = 200;
    private Object data;
    
   
	/**
	 * Constructor.
	 */
	public ReturnCodeDto() {
		
	}
	
	/**
	 * 失败
	 * @param retCode
	 * @param message
	 */
	public ReturnCodeDto( boolean success , Object data) {
		this.success = success;
		if(success){
			this.data = data;
		}else{
			this.message = data.toString();
		}
	}
	
	/**
	 * 成功
	 * @param retCode
	 * @param message
	 */
	public ReturnCodeDto(Object obj) {
		this.success = true;
		this.data=obj;
	}

	/**
	 * @param retCode
	 * @param message
	 */
	public ReturnCodeDto(boolean success, String message ,Object obj) {
		this.success = success;
		this.message = message;
		this.data=obj;
	}
	
	
	/**
	 * @param retCode
	 * @param message
	 */
	public ReturnCodeDto(boolean success, String message ,Object obj,int statusCode) {
		this.success = success;
		this.message = message;
		this.data=obj;
		this.statusCode=statusCode;
	}
	
	/**
	 * @return the success
	 */
	public boolean isSuccess() {
		return success;
	}

	/**
	 * @param  设置 success
	 */
	public void setSuccess(boolean success) {
		this.success = success;
	}

	/**
	 * @return the message
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * @param  设置 message
	 */
	public void setMessage(String message) {
		this.message = message;
	}

	/**
	 * @return the statusCode
	 */
	public int getStatusCode() {
		return statusCode;
	}

	/**
	 * @param  设置 statusCode
	 */
	public void setStatusCode(int statusCode) {
		this.statusCode = statusCode;
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

	/*
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		if(success && data != null){
			if(JSONUtils.isArray(data)){
				return "ReturnCodeDto [success=" + success + ", message=" + message + ", data="+JSONArray.fromObject(data).toString()+"]";
			}else if( !JSONUtils.isObject(data)){
				return "ReturnCodeDto [success=" + success + ", message=" + message + ", data="+data.toString()+"]";
			}else if( JSONUtils.isObject(data) && data instanceof CommonDto){
				return "ReturnCodeDto [success=" + success + ", message=" + message + ", data="+JSONObject.fromObject(data).toString()+"]";
			}
			return "ReturnCodeDto [success=" + success + ", message=" + message + ", data="+data+"]";
		}
		return "ReturnCodeDto [success=" + success + ", message=" + message + ", data="+data+"]";
	}

}
