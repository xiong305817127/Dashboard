package com.xh.util;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;


public class ProxyObjectUtil{
	
	@SuppressWarnings("unchecked")
	public static <T> T getProxyObject(Class<T> interfaceClass ,Object obj){
		T proxySubject = (T)Proxy.newProxyInstance(interfaceClass.getClassLoader(),  new Class[]{interfaceClass},  new ProxyHandler(obj));
		return proxySubject;
	}
	
	public static void main(String[] a){
		
	}
	
	
}

class ProxyHandler implements InvocationHandler {

	private Object proxied;

	public ProxyHandler(Object proxied) {
		this.proxied = proxied;
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		 return method.invoke( proxied, args);  
	}

}


