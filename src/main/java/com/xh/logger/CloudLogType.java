/**
 * 云化数据集成系统 
 * iDatrix CloudETL
 */
package com.xh.logger;

/**
 * CloudLogType.java
 * @author JW
 * @since 2017年8月3日
 *
 */
public enum CloudLogType {

	USER_LOG("user", ".log");

	// non-standard, Kettle database repository only!
	//
	// USER("user", ".usr"),

	private String type;
	private String extension;

	private CloudLogType(String type, String extension) {
		this.type = type;
		this.extension = extension;
	}

	@Override
	public String toString() {
		return type;
	}

	public String getType() {
		return type;
	}

	public String getExtension() {
		return extension;
	}

}
