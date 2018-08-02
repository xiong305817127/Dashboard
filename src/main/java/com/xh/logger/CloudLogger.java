/**
 * 云化数据集成系统 
 * iDatrix CloudETL
 */
package com.xh.logger;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.logging.log4j.Logger;

import com.xh.util.ConfigPropertyUtil;
import com.xh.util.UserHolder;
import com.xh.util.Utils;

/**
 * Cloud logger implementation. <br/>
 * (Need to re-design and implement cloud ETL logger function in V01R02.)
 * 
 * @author JW
 * @since 05-12-2017
 *
 */
public class CloudLogger {
	
	public static final Log  logger = LogFactory.getLog("CloudLogger");
	
	 public static final AtomicLong Num = new AtomicLong( 1 );

	private final static String DEFAULT_LOG_DIR = "./logs";
	private final static String LOG_NAME = "cloudetl";
	private final static String DEFAULT_LOGGER_NAME = "CloudLog";
	private final static String LOGGER_PREFIX = "logger_for_";
	private final static String DEFAULT_LOGGER_TEXT_HEAD = "Cloud";
	
	private static HashMap<String, CloudLogger> userLoggers;

	private Logger log;

	private CloudLogger() {
		log = CloudLoggerFactory.getLogger(DEFAULT_LOGGER_NAME);
	}

	private CloudLogger(String name, String path, String filename, String extension) {
		log = CloudLoggerFactory.createLogger(name, path, filename, extension);
	}
	
	public synchronized static CloudLogger getInstance() {
			String username = UserHolder.getUserName();
			return getInstance(username);
	}

	public synchronized static CloudLogger getInstance(String username) {
		try {
			if (Utils.isEmpty(username)) {
				return new CloudLogger();
			}
			if (userLoggers != null && userLoggers.containsKey(username)) {
				return userLoggers.get(username);
			} else if (userLoggers == null) {
				userLoggers = new HashMap<>();
			}
			
			String rootpath = ConfigPropertyUtil.getProperty("log.user.root.path",DEFAULT_LOG_DIR);
			String logpath  ;
			if( rootpath.endsWith("/")||rootpath.endsWith("\\")) {
				logpath = rootpath +username+"/";
			}else {
				logpath = rootpath+"/"+username+"/";
			}
			CloudLogger logger = new CloudLogger(LOGGER_PREFIX + username,logpath, LOG_NAME, CloudLogType.USER_LOG.getExtension());
			
			userLoggers.put(username, logger);
			return logger;
			
		} catch (Exception e) {
			logger.error("初始化用户日志失败.",e);
			return new CloudLogger();
		}
	}

	public static String getAsUtf8(String logText) {
		try {
			return new String(logText.getBytes(), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			return logText;
		}
	}

	public static String logMessage2(String logHead, Object... logObjs) {
		if (logHead.contains("{}")) {
			return getAsUtf8(String.format(logHead.replaceAll("\\{\\}", "%s"), logObjs));
		}

		StringBuilder sb = new StringBuilder(logHead);
		if(logObjs != null ) {
			for (Object logObj : logObjs) {
				sb.append(" ");
				if (logObj == null) {
					sb.append("NULL");
				} if(logObj instanceof String ){
					sb.append(logObj);
				}else {
					sb.append(CloudLogUtils.jsonLog(logObj));
				}
			}
		}else {
			sb.append("<NULL>");
		}
		

		return getAsUtf8(sb.toString());
	}

	public void info(String logText) {
		info(null,(Long)null, logText);
	}
	
	public  void info(Object logHead ,Object... logText) {
		info(logHead, (Long)null ,logText);
	}
	
	public  void info(Object logHead ,Long numberFlag , Object... logText) {
		try {
			String head = getLogTextHead(logHead, numberFlag);
			log.info(logMessage2(head,logText));
		} catch (Exception e) {
		}
	}


	public  void warn(String logText) {
		warn(null, (Long)null,logText);
	}
	
	public  void warn(Object logHead ,Object... logText) {
		warn(logHead , (Long)null, logText);
	}
	
	public  void warn(Object logHead ,Long numberFlag ,Object... logText) {
		try {
			String head =  getLogTextHead(logHead, numberFlag);
			log.warn(logMessage2(head,logText));
		} catch (Exception e) {
		}
	}

	public  void error(String logText) {
		error(null,(Long)null, logText);
	}
	
	public  void error(Object logHead ,String logText) {
		error(logHead ,(Long)null, logText);
	}
	
	public  void error(Object logHead ,Long numberFlag ,String logText) {
		try {
			String head = getLogTextHead(logHead, numberFlag);
			log.error(logMessage2(head,logText));
		} catch (Exception e) {
		}
	}

	public  void error(String message, Throwable t) {
		error(null, (Long)null , message, t);
	}
	
	public  void error(Object logHead ,String message, Throwable t) {
		error(logHead,(Long)null , message, t);
	}
	
	public  void error(Object logHead ,Long numberFlag  , String message, Throwable t) {
		try {
			String head = getLogTextHead(logHead, numberFlag);
			log.error(logMessage2(head,message), t);
		} catch (Exception e) {
		}
	}

	public  void debug(String logText) {
		debug(null, (Long)null,logText);
	}
	
	public  void debug(Object logHead ,Object... logText) {
		debug(logHead, (Long)null, logText);
	}

	public  void debug(Object logHead ,Long numberFlag,Object... logText) {
		try {
			String head = getLogTextHead(logHead, numberFlag);
			log.debug(logMessage2(head, logText));
		} catch (Exception e) {
		}
	}
	

	public  void trace(Object message) {
		trace(null, (Long)null,message);
	}
	
	public  void trace(Object logHead ,Object... message) {
		trace(logHead,  (Long)null, message);
	}
	
	public  void trace(Object logHead ,Long numberFlag,Object... message) {
		try {
			String head = getLogTextHead(logHead, numberFlag);
			log.trace(logMessage2(head, message));
		} catch (Exception e) {
		}
	}

	public  void trace(Object message, Throwable t) {
		trace(null, (Long)null, t, message);
	}
	
	public  void trace(Object logHead, Throwable t  ,Object... message) {
		trace(logHead,  (Long)null, t, message);
	}
	
	public  void trace(Object logHead,Long numberFlag , Throwable t,Object... message) {
		try {
			String head = getLogTextHead(logHead, numberFlag);
			log.trace(logMessage2(head, message), t);
		} catch (Exception e) {
		}
	}

	
	private  String getLogTextHead(Object logHead , Long number) {
		
		String head = "" ;
		if(logHead != null) {
			if( logHead instanceof String ) {
				head = "["+logHead.toString();
			}else if( logHead instanceof Class ){
				head = "["+((Class<?>)logHead).getSimpleName();
			}else {
				head = "["+logHead.getClass().getSimpleName();
			}
		}else {
			head= "["+DEFAULT_LOGGER_TEXT_HEAD ;
		}
		if(number != null) {
			head = head+":"+number;
		}
		return head+"]" ;
	}
	
	/**
	 * 生成界面上显示的异常提示消息
	 * @param e
	 * @return
	 */
	public static String getExceptionMessage(Throwable e){
		String errmsg = getExceptionMessage(e,true);
		errmsg = Utils.isEmpty(errmsg) ? "出现异常, 请查看服务器详细日志!" : (errmsg.length() > 120 ? errmsg.substring(0, 120) : errmsg);
		return errmsg;
	}

	/**
	 * 生成界面上显示的异常提示消息
	 * @param e
	 * @param isRoot - 是否打印异常堆栈
	 * @return
	 */
	private static String getExceptionMessage(Throwable e,boolean isRoot){
		if(e == null || e.getClass() == null){
			return "";
		}

		StringBuilder sb = new StringBuilder(" ");

		if(e.getCause() != null){
			String resultCause = getExceptionMessage(e.getCause(),false);
			if(!Utils.isEmpty(resultCause)){
				sb.append(resultCause);
			}
		} else {
			//sb.append("原因: ");

			String result = e.getMessage() != null ? e.getMessage().trim() : "";;
			if(!Utils.isEmpty(result)) { //&& !result.trim().contains("\n")){
				sb.append(result); //.split("\\n")[0].trim());
			} else {
				sb.append(e.getClass().getSimpleName());
			}
		}
		return sb.toString();
	}
	// Below functions are only for developing
	//

	public static String seekLogString(Object logObj) {
		if (logObj == null) {
			return "NULL";
		} else if (logObj.getClass().isArray()) {
			Object[] objArr = (Object[]) logObj;
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < objArr.length; i++) {
				sb.append(seekLogString(objArr[i]));
				if (i < objArr.length - 1)
					sb.append(", ");
			}
			return sb.toString();
		} else if ("List".equals(logObj.getClass().getSimpleName())
				|| "ArrayList".equals(logObj.getClass().getSimpleName())) {
			@SuppressWarnings("unchecked")
			List<Object> objLst = (List<Object>) logObj;
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < objLst.size(); i++) {
				sb.append(seekLogString(objLst.get(i)));
				if (i < objLst.size() - 1)
					sb.append(", ");
			}
			return sb.toString();
		} else {
			return String.valueOf(logObj);
		}
	}

}
