/**
 * 云化数据集成系统 
 * iDatrxi CloudETL
 */
package com.xh.logger;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Scanner;

import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;

import com.xh.common.CommonException;
import com.xh.util.Utils;
import com.xh.util.vfs.WebVFS;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.util.JSONUtils;

/**
 * CloudLogUtils <br/>
 * @author JW
 * @since 2017年9月8日
 * 
 */
public class CloudLogUtils {

	public static String jsonLog(Object obj) {
		try {
			return JSONUtils.isArray(obj) ? JSONArray.fromObject(obj).toString() : JSONObject.fromObject(obj).toString();
		} catch (Exception e) {
			return obj != null ? obj.toString() : "NULL";
		}
	}

	public static String jsonLog2(Object obj) {
		try {
			return obj.getClass().getSimpleName() + ": "
					+ (JSONUtils.isArray(obj) ? JSONArray.fromObject(obj).toString() : JSONObject.fromObject(obj).toString());
		} catch (Exception e) {
			return obj != null ? obj.toString() : "NULL";
		}
	}

	public static String exStackTraceLog(Exception ex) {
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw, true);
		ex.printStackTrace(pw);
		pw.flush();
		sw.flush();
		pw.close();
		return sw.toString();
	}
	
	public static String searchLog( String fileName, String startLineFlag ,String endLineFlag) throws CommonException{//在文件a中的每行中查找x
		
		StringBuilder sb = new StringBuilder();
		Scanner scan = null;
		FileObject fileObject = null ;
		boolean isEnd = false;
		boolean isStart = false ;
		if(Utils.isEmpty(startLineFlag)) {
			isStart = true ;
		}
	
		try {
			fileObject = WebVFS.getFileObject(fileName);
			if (fileObject.exists()) {
				 scan  = new Scanner(fileObject.getContent().getInputStream(),"UTF-8");
		         while(scan.hasNext() && !isEnd){    
		            String s = scan.nextLine();
		            
		            if(isStart) {
		            	sb.append(s+Utils.CR);
		            }
		            
		            if(!Utils.isEmpty(startLineFlag) && s.contains(startLineFlag)){
		            	isStart = true ;
		            }
		            
		            if(!Utils.isEmpty(endLineFlag) &&s.contains(endLineFlag)){
		            	isEnd = true ;
		            }
		        } 
			}
	        
		} catch ( Exception e) {
			sb.append( "Unable to get log from file [" + fileName + "],"+e.getMessage());
		} finally {
			if(scan != null) {
				scan.close();
			}
			if(fileObject != null) {
				try {
					fileObject.close();
				} catch (FileSystemException e) {
				}
			}
		}
		
		return sb.toString();
    }

	public static String getLog(String filename) throws CommonException {
		String logText = "";

		InputStream inputStream = null;
		try {
			FileObject fileObject = WebVFS.getFileObject(filename);
			if (fileObject.exists()) {
				inputStream = WebVFS.getInputStream(fileObject);
				byte b[]=new byte[(int) fileObject.getContent().getSize()];
				inputStream.read(b);
				inputStream.close();
				logText = new String(b);
			}
			fileObject.close();
		} catch ( Exception e) {
			throw CommonException.parseException(e);
		} finally {
			if (inputStream != null) {
				try {
					inputStream.close();
				} catch (IOException e) {
				}
			}
		}

		return logText;
	}

	public static void insertLog(String filename, String logText) throws CommonException{
		
		OutputStream outputStream = null;
		try {
			FileObject fileObject = WebVFS.getFileObject(filename);
			if (!fileObject.exists()) {
				fileObject.createFile();
			}
			fileObject.close();
		
			outputStream = WebVFS.getOutputStream(filename, true);
			outputStream.write(logText.getBytes());
			outputStream.write(Utils.CR.getBytes());
			outputStream.close();
		} catch ( Exception e) {
			throw CommonException.parseException(e);
		} finally {
			if (outputStream != null) {
				try {
					outputStream.close();
				} catch (IOException e) {
				}
			}
		}
	}

	public static void renameLog( String filename, String newPath) throws CommonException  {
		try {
			FileObject fileObject = WebVFS.getFileObject(filename);
			if (fileObject.exists()) {
				FileObject newObject = WebVFS.getFileObject( newPath );
				fileObject.moveTo(newObject); // JW: maybe failed !
				newObject.close();
			}
			fileObject.close();
		} catch (FileSystemException e) {
			throw CommonException.parseException(e);
		}
	}

	public static void deleteLog(String filename) throws CommonException  {
		try {
			FileObject fileObject = WebVFS.getFileObject(filename);
			if (fileObject.exists()) {
				if(fileObject.isFolder()) {
					fileObject.deleteAll();
				}else {
					fileObject.delete();
				}
			}
			fileObject.close();
		} catch (FileSystemException e) {
			throw CommonException.parseException(e);
		}
	}
	
	public static void deleteAllLog(String filepath, String fileName) throws CommonException  {
		try {
			FileObject fileObject = WebVFS.getFileObject(filepath);
			if(fileObject.isFolder()) {
				for( FileObject child : fileObject.getChildren()) {
					if (child.exists() && child.getName().getBaseName().contains(fileName) ) {
						if(child.isFolder()) {
							child.deleteAll();
						}else {
							child.delete();
						}
					}
					child.close();
				}
			}else {
				if (fileObject.exists()) {
					fileObject.delete();
				}
			}
			fileObject.close();
		} catch (FileSystemException e) {
			throw CommonException.parseException(e);
		}
	}

}
