package com.xh.util.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.xh.common.exception.WebException;
import com.xh.util.Utils;
import com.xh.util.http.callback.HttpCallbackModelListener;
import com.xh.util.http.callback.HttpCallbackStringListener;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.util.JSONUtils;

/**
 * HttpURLConnection 网络请求工具类
 *
 * 数据的请求都是基于HttpURLConnection的 请求成功与失败的回调都是在主线程
 */
public class HttpUtils {

	static ExecutorService threadPool = Executors.newCachedThreadPool();

	private final static int ConnectTimeout = 5000;
	private final static int ReadTimeout = 10000;

	/**
	 * 异步调用GET方法 返回数据会解析成字符串String
	 * 
	 * @param urlString
	 *            请求的url
	 * @param listener
	 *            回调监听
	 */
	public static void doAsyncGet(String urlString, HttpCallbackStringListener listener,Object params,Map<String,String> headers) {
		doAsyncGeneralGet(urlString, "GET", listener, params, headers);
	}

	/**
	 * 同步执行GET方法 返回数据会解析成String
	 * 
	 * @param urlString
	 *            请求的url
	 * @throws IOException
	 * @throws WebException
	 */
	public static String doGet(String urlString,Object params,Map<String,String> headers) throws WebException, IOException {
		return doGeneralGet(urlString, "GET", params, headers);
	}

	/**
	 * 异步调用GET方法 返回数据会解析成cls对象
	 * 
	 * @param urlString
	 *            请求路径
	 * @param listener
	 *            回调监听
	 * @param cls
	 *            返回的对象
	 * @param <T>
	 *            监听的泛型
	 */
	public static <T> void doAsyncGet(String urlString, HttpCallbackModelListener<T> listener, final Class<T> cls,Object params,Map<String,String> headers) {
		doAsyncGeneralGet(urlString, "GET", listener, cls, params, headers);
	}

	/**
	 * 同步执行GET方法 返回数据会解析成cls对象
	 * 
	 * @param urlString
	 *            请求的url
	 * @param cls
	 *            返回的对象
	 * @param <T>
	 * @throws IOException
	 * @throws WebException
	 */
	public static <T> T doGet(String urlString, final Class<T> cls,Object params,Map<String,String> headers) throws WebException, IOException {
		return doGeneralGet(urlString, "GET", cls, params, headers) ;
	}
	
	
	
	/**
	 * 异步调用Method方法 返回数据会解析成字符串String
	 * 
	 * @param urlString
	 *            请求的url
	 * @param method    GET/HEAD/DELETE  
	 * @param listener
	 *            回调监听
	 */
	public static void doAsyncGeneralGet(String urlString, String method ,HttpCallbackStringListener listener,Object params,Map<String,String> headers) {
		// 因为网络请求是耗时操作，所以需要另外开启一个线程来执行该任务。
		threadPool.execute(new Runnable() {
			@Override
			public void run() {
				try {
					String result = doConnectionGet(urlString,method,params,headers);
					if (listener != null) {
						listener.onFinish(result);
					}
				} catch (WebException | IOException e) {
					if (listener != null) {
						// 回调onError()方法
						listener.onError(e);
					} else {
						e.printStackTrace();
					}
				}
			}
		});
	}

	/**
	 * 同步执行Method方法 返回数据会解析成String
	 * 
	 * @param urlString
	 *            请求的url
	 * @param method    GET/HEAD/DELETE  
	 * @throws IOException
	 * @throws WebException
	 */
	public static String doGeneralGet(String urlString,String method ,Object params,Map<String,String> headers) throws WebException, IOException {
		return doConnectionGet(urlString,method,params,headers);
	}

	/**
	 * 异步调用Method方法 返回数据会解析成cls对象
	 * 
	 * @param urlString
	 *            请求路径
	 * @param method    GET/HEAD/DELETE  
	 * @param listener
	 *            回调监听
	 * @param cls
	 *            返回的对象
	 * @param <T>
	 *            监听的泛型
	 */
	public static <T> void doAsyncGeneralGet(String urlString,String method, HttpCallbackModelListener<T> listener, final Class<T> cls,Object params,Map<String,String> headers) {
		// 因为网络请求是耗时操作，所以需要另外开启一个线程来执行该任务。
		threadPool.execute(new Runnable() {
			@SuppressWarnings("unchecked")
			@Override
			public void run() {
				try {
					String result = doConnectionGet(urlString,method,params,headers);
					if (JSONUtils.mayBeJSON(result) && result.startsWith("{")) {
						JSONObject resultJson = JSONObject.fromObject(result);
						if (listener != null) {
							listener.onFinish((T) JSONObject.toBean(resultJson, cls));
						}
					} else {
						listener.onError(new WebException("Getting information is not in JSON format:" + result));
					}

				} catch (WebException | IOException e) {
					if (listener != null) {
						// 回调onError()方法
						listener.onError(e);
					} else {
						e.printStackTrace();
					}
				}
			}
		});
	}

	/**
	 * 同步执行Method方法 返回数据会解析成cls对象
	 * 
	 * @param urlString
	 *            请求的url
	 * @param method    GET/HEAD/DELETE           
	 * @param cls
	 *            返回的对象
	 * @param <T>
	 * @throws IOException
	 * @throws WebException
	 */
	@SuppressWarnings("unchecked")
	public static <T> T doGeneralGet(String urlString, String method , Class<T> cls,Object params,Map<String,String> headers) throws WebException, IOException {
		String result = doConnectionGet(urlString,method,params,headers);
		if (JSONUtils.mayBeJSON(result) && result.startsWith("{")) {
			JSONObject resultJson = JSONObject.fromObject(result);
			return (T) JSONObject.toBean(resultJson, cls);
		} else {
			throw new WebException("Getting information is not in JSON format:" + result);
		}
	}

	/**
	 * 
	 * @param urlString
	 * @param method    GET/HEAD/DELETE
	 * @param params   String/Map/Object
	 * @param headers
	 * @return
	 * @throws WebException
	 * @throws IOException
	 */
	@SuppressWarnings("rawtypes")
	private static String doConnectionGet(String urlString,String method ,Object params,Map<String,String> headers) throws WebException, IOException {
		URL url = null;
		HttpURLConnection httpURLConnection = null;
		InputStream is = null;
		BufferedReader bf = null;
		try {
			// 组织请求参数
			if(params != null){
				Map paramsMap = null ;
				if (params instanceof Map) {
					paramsMap = (Map) params;
				}else if (JSONUtils.isObject(params) ) {
					paramsMap = Utils.newHashMap();
					JSONObject.fromObject(params).putAll(paramsMap);
				}
				String paramsStr = params.toString() ;
				if( paramsMap != null){
					final StringBuffer out = new StringBuffer();
					if (params instanceof Map) {
						for (Object key : paramsMap.keySet()) {
							if (out.length() != 0) {
								out.append("&");
							}
							out.append(key).append("=").append(paramsMap.get(key));
						}
					}
					paramsStr = out.toString();
				}
				if( urlString.indexOf("?") != -1 ){
					if(urlString.endsWith("?")){
						urlString = urlString+paramsStr ;
					}else{
						urlString = urlString+"&"+paramsStr ;
					}
				}else{
					urlString = urlString+"?"+paramsStr ;
				}
			}
			 
			// 根据URL地址创建URL对象
			url = new URL(urlString);
			// 获取HttpURLConnection对象
			httpURLConnection = (HttpURLConnection) url.openConnection();
			// 设置请求方式，默认为GET
			httpURLConnection.setRequestMethod(method);
			//设置header
			if(headers!= null && headers.size() >0){
				for(String key : headers.keySet()){
					httpURLConnection.setRequestProperty(key, headers.get(key));
				}
			}
			
			// 设置连接超时
			httpURLConnection.setConnectTimeout(ConnectTimeout);
			// 设置读取超时
			httpURLConnection.setReadTimeout(ReadTimeout);
			// 响应码为200表示成功，否则失败。
			if ((httpURLConnection.getResponseCode() / 100 - 2) == 0) {
				// 获取网络的输入流
				is = httpURLConnection.getInputStream();
				bf = new BufferedReader(new InputStreamReader(is, "UTF-8"));
				// 最好在将字节流转换为字符流的时候 进行转码
				StringBuffer buffer = new StringBuffer();
				String line = "";
				while ((line = bf.readLine()) != null) {
					buffer.append(line);
				}
				return buffer.toString();
			} else {
				throw new WebException("response err code:" + httpURLConnection.getResponseCode());
			}
		} finally {
			if (bf != null) {
				bf.close();
			}
			if (is != null) {
				is.close();
			}
			if (httpURLConnection != null) {
				// 释放资源
				httpURLConnection.disconnect();
			}
		}

	}

	/**
	 * 异步调用Post方法 返回数据会解析成字符串String
	 * 
	 * @param urlString
	 *            请求的路径
	 * @param params
	 *            参数列表,Map对象为form提交,Object/Array为json提交,剩下对象直接提交
	 * @param listener
	 *            回调监听   
	 */
	public static void doAsyncPost(String urlString, Object params,HttpCallbackStringListener listener,Map<String,String> headers) {
		doAsyncGeneralPost(urlString, "POST", params, listener, headers);
	}

	/**
	 * 同步执行POST方法 返回数据会解析成String
	 * 
	 * @param urlString
	 *            请求的路径
	 * @param params
	 *            参数列表,Map对象为form提交,Object/Array为json提交,剩下对象直接提交
	 * @throws IOException 
	 * @throws WebException 
	 */
	public static String doPost(String urlString, Object params,Map<String,String> headers) throws WebException, IOException {
		return doGeneralPost(urlString, "POST", params, headers);
	}



	/**
	 * /** 异步POST方法 返回数据会解析成cls对象
	 * 
	 * @param urlString
	 *            请求的路径
	 * @param params
	 *            参数列表,Map对象为form提交,Object/Array为json提交,剩下对象直接提交
	 * @param listener
	 *            回调监听
	 * @param cls
	 *            对象
	 * @param <T>
	 *            监听泛型
	 */
	public static <T> void doAsyncPost(  String urlString, Object params,  HttpCallbackModelListener<T> listener, Class<T> cls,Map<String,String> headers){
		 doAsyncGeneralPost(urlString, "POST", params, listener, cls, headers);
	}
	
	/**
	 * /** 同步POST方法 返回数据会解析成cls对象
	 * 
	 * @param urlString
	 *            请求的路径
	 * @param params
	 *            参数列表,Map对象为form提交,Object/Array为json提交,剩下对象直接提交
	 * @param cls
	 *            对象
	 * @param <T>
	 *            监听泛型
	 * @throws IOException 
	 * @throws WebException 
	 */
	public static <T> T doPost(  String urlString, Object params,  Class<T> cls,Map<String,String> headers) throws WebException, IOException {
		return doGeneralPost(urlString, "POST", params, cls, headers);
	}
	
	
	/**
	 * 异步调用Method方法 返回数据会解析成字符串String
	 * 
	 * @param urlString
	 *            请求的路径
	 * @param method
	 *             POST/PUT
	 * @param params
	 *            参数列表,Map对象为form提交,Object/Array为json提交,剩下对象直接提交
	 * @param listener
	 *            回调监听   
	 */
	public static void doAsyncGeneralPost(String urlString,String method , Object params,HttpCallbackStringListener listener,Map<String,String> headers) {
		// 因为网络请求是耗时操作，所以需要另外开启一个线程来执行该任务。
		threadPool.execute(new Runnable() {
			@Override
			public void run() {
				try {
					String result = doConnectionPost(urlString,method, params,headers);
					if (listener != null) {
						listener.onFinish(result);
					}
				} catch (WebException | IOException e) {
					if (listener != null) {
						// 回调onError()方法
						listener.onError(e);
					} else {
						e.printStackTrace();
					}
				}
			}
		});
	}

	/**
	 * 同步执行Method方法 返回数据会解析成String
	 * 
	 * @param urlString
	 *            请求的路径
	 * @param method
	 *             POST/PUT
	 * @param params
	 *            参数列表,Map对象为form提交,Object/Array为json提交,剩下对象直接提交
	 * @throws IOException 
	 * @throws WebException 
	 */
	public static String doGeneralPost(String urlString,String method , Object params,Map<String,String> headers) throws WebException, IOException {
		return doConnectionPost(urlString,method, params,headers);
	}



	/**
	 * /** 异步Method方法 返回数据会解析成cls对象
	 * 
	 * @param urlString
	 *            请求的路径
	 * @param method
	 *             POST/PUT
	 * @param params
	 *            参数列表,Map对象为form提交,Object/Array为json提交,剩下对象直接提交
	 * @param listener
	 *            回调监听
	 * @param cls
	 *            对象
	 * @param <T>
	 *            监听泛型
	 */
	public static <T> void doAsyncGeneralPost(  String urlString, String method ,Object params,  HttpCallbackModelListener<T> listener, Class<T> cls,Map<String,String> headers){
				// 因为网络请求是耗时操作，所以需要另外开启一个线程来执行该任务。
				threadPool.execute(new Runnable() {
					@SuppressWarnings("unchecked")
					@Override
					public void run() {
						try {
							String result = doConnectionPost(urlString,method, params,headers);
							if (JSONUtils.mayBeJSON(result) && result.startsWith("{")) {
								JSONObject resultJson = JSONObject.fromObject(result);
								if (listener != null) {
									listener.onFinish((T) JSONObject.toBean(resultJson, cls));
								}
							} else {
								listener.onError(new WebException("Getting information is not in JSON format:" + result));
							}

						} catch (WebException | IOException e) {
							if (listener != null) {
								// 回调onError()方法
								listener.onError(e);
							} else {
								e.printStackTrace();
							}
						}
					}
				});
	}
	
	/**
	 * /** 同步Method方法 返回数据会解析成cls对象
	 * 
	 * @param urlString
	 *            请求的路径 POST/PUT
	 * @param method
	 *             POST/PUT
	 * @param params
	 *            参数列表,Map对象为form提交,Object/Array为json提交,剩下对象直接提交
	 * @param cls
	 *            对象
	 * @param <T>
	 *            监听泛型
	 * @throws IOException 
	 * @throws WebException 
	 */
	@SuppressWarnings("unchecked")
	public static <T> T doGeneralPost(  String urlString,String method , Object params,  Class<T> cls,Map<String,String> headers) throws WebException, IOException {
		String result = doConnectionPost(urlString,method, params,headers);
		if (JSONUtils.mayBeJSON(result) && result.startsWith("{")) {
			JSONObject resultJson = JSONObject.fromObject(result);
			return (T) JSONObject.toBean(resultJson, cls);
		} else {
			throw new WebException("Getting information is not in JSON format:" + result);
		}
	}
	

	


	/**
	 * 
	 * @param urlString
	 *            请求的路径
	 * @param method
	 *             POST/PUT
	 * @param params
	 *            参数列表,Map对象为form提交,Object/Array为json提交,剩下对象直接提交
	 * @param headers
	 * @throws IOException 
	 * @throws WebException 
	 */
	@SuppressWarnings("rawtypes")
	private static String doConnectionPost(String urlString, String method ,Object params,Map<String,String> headers) throws WebException, IOException {

		URL url;
		HttpURLConnection httpURLConnection = null;
		PrintWriter printWriter = null ;
		InputStream is = null;
		BufferedReader bf = null ;
		try {
			url = new URL(urlString);
			httpURLConnection = (HttpURLConnection) url.openConnection();
			
			final StringBuffer out = new StringBuffer();
			// 组织请求参数
			if (params instanceof Map) {
				Map paramsMap = (Map) params;
				for (Object key : paramsMap.keySet()) {
					if (out.length() != 0) {
						out.append("&");
					}
					out.append(key).append("=").append(paramsMap.get(key));
				}
				httpURLConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			} else if (JSONUtils.isObject(params) ) {
				out.append(JSONObject.fromObject(params).toString());
				httpURLConnection.setRequestProperty("Content-Type", "application/json");
			}else if( JSONUtils.isArray(params)){
				out.append(JSONArray.fromObject(params).toString());
				httpURLConnection.setRequestProperty("Content-Type", "application/json");
			} else {
				out.append(JSONUtils.valueToString(params));
				httpURLConnection.setRequestProperty("Content-Type", "text/plain");
			}
			
			httpURLConnection.setRequestProperty("accept", "*/*");
			httpURLConnection.setRequestProperty("connection", "Keep-Alive");
			httpURLConnection.setRequestProperty("Content-Length", String.valueOf(out.length()));
			httpURLConnection.setRequestProperty("Accept-Charset", "utf-8"); 
			httpURLConnection.setRequestMethod(method);
			
			//设置header
			if(headers!= null && headers.size() >0){
				for(String key : headers.keySet()){
					httpURLConnection.setRequestProperty(key, headers.get(key));
				}
			}

			httpURLConnection.setConnectTimeout(ConnectTimeout);
			httpURLConnection.setReadTimeout(ReadTimeout);

			// 设置运行输入
			httpURLConnection.setDoInput(true);
			// 设置运行输出
			httpURLConnection.setDoOutput(true);
			if("POST".equalsIgnoreCase(method)){
				httpURLConnection.setUseCaches(false);
				httpURLConnection.setInstanceFollowRedirects(true);
			}
			httpURLConnection.connect();  
			
			printWriter = new PrintWriter(httpURLConnection.getOutputStream());
			// 发送请求参数
			printWriter.write(out.toString());
			// flush输出流的缓冲
			printWriter.flush();

			if ((httpURLConnection.getResponseCode() / 100 - 2) == 0) {
				// 获取网络的输入流
				is = httpURLConnection.getInputStream();
				bf = new BufferedReader(new InputStreamReader(is, "UTF-8"));
				// 最好在将字节流转换为字符流的时候 进行转码
				StringBuffer buffer = new StringBuffer();
				String line = "";
				while ((line = bf.readLine()) != null) {
					buffer.append(line);
				}
				return buffer.toString();
			} else {
				throw new WebException("response err code:" + httpURLConnection.getResponseCode());
			}
		} finally {
			if(printWriter != null ){
				printWriter.close();
			}
			if( is != null){
				is.close();
			}
			if( bf != null){
				bf.close();
			}
			if (httpURLConnection != null) {
				// 最后记得关闭连接
				httpURLConnection.disconnect();
			}
		}
	}

}
