/**
 * GDBD iDatrix CloudETL System.
 */
package com.xh.util;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * Provides utilities interactive with OSGI bundle.
 * 	- Load OSGI bundle (TODO.)
 *  - Invoke OSGI bundle class method
 * Since OSGI container has its own BundleClassLoader,
 * we need to reflect a bundle class to get its object
 * and then invoke its methods.
 * Please do not instantiate a bundle class outside of
 * the OSGI container (Apache Karaf in this project)!
 * 
 * @author JW
 * @since 2017年6月20日
 *
 */
public class ReflectUtils {

	protected final static Log logger = LogFactory.getLog(ReflectUtils.class);
	/**
	 * Invoke method in class loaded in OSGI bundle. !!! deprecated - It will
	 * cause problem if given null as the parameter!
	 * 
	 * @param obj
	 *            class | Object , class 获取静态方法,Object 获取对象方法
	 * @param name
	 * @param args
	 * @return
	 */
	public static Object invokeOsgiMethod(Object obj, String name, Object... args) {
		if (obj == null) {
			return null;
		}
		Class<?>[] types = null;
		if (args != null) {
			types = new Class[args.length];
			for (int i = 0; i < args.length; i++) {
				if (args[i] != null) {
					// it will cause problem if given null as the parameter!
					types[i] = args[i].getClass();
				}
			}
		}
		return invokeOsgiMethod(obj, name, args, types);
	}

	/**
	 * Invoke method in class loaded in OSGI bundle.
	 * 
	 * @param obj
	 * @param name
	 * @param args
	 * @param argTypes
	 * @return
	 */
	public static Object invokeOsgiMethod(Object obj, String name, Object[] args, Class<?>[] argTypes) {
		
		if (obj == null) {
			return null;
		}
		Class<?> objClass;
		if (obj instanceof Class) {
			objClass = (Class<?>) obj;
			obj = null;
		} else if (obj instanceof String) {
			try {
				objClass = Class.forName((String) obj);
			} catch (ClassNotFoundException e) {
				logger.info("invoke Osgi Method error, obj: " + obj + " Class Not Found! " + e.getMessage());
				return null;
			}
			obj = null;
		} else {
			objClass = obj.getClass();
		}
		Method m;
		if (argTypes != null && argTypes.length > 0) {
			m = seekOsgiMethod(objClass, name, argTypes);
		} else {
			m = seekOsgiMethod(objClass, name);
		}
		if (m != null) {
			// 取消java语言访问检查以访问protected方法
			m.setAccessible(true);
			try {
				if (args != null && args.length == 1 && m.getParameterTypes() != null&& m.getParameterTypes().length == 1 && m.getParameterTypes()[0].isArray()) {
					Class<?> paramType = m.getParameterTypes()[0].getComponentType();
					Class<?> array = objClass.getClassLoader().loadClass("java.lang.reflect.Array");
					Object argsArr = array.getMethod("newInstance", Class.class, int.class).invoke(null, paramType,((Object[]) args[0]).length);// ((Object[])args[0]).length);
					for (int i = 0; i < ((Object[]) args[0]).length; i++) {
						array.getMethod("set", Object.class, int.class, Object.class).invoke(null, argsArr, i,((Object[]) args[0])[i]);
					}
					return m.invoke(obj, argsArr);
				} else {
					return m.invoke(obj, args);
				}

			} catch (Exception e) {
				logger.info(" DEBUG >>>invoke Osgi Method error, Name: " + m.getName() + " ,error " + e.getMessage());
				// e.printStackTrace();
			}
		}
		return null;
	}

	/**
	 * Create a new instance of class loaded in OSGI bundle. - It's better
	 * calling PluginRegistry to find the plugin and load class.
	 * 
	 * @param obj
	 * @param className
	 * @param args
	 * @return
	 */
	public static Object newOsgiInstance(Object obj, String className, Object... args) {
		Class<?>[] types = null;
		if (args != null && args.length > 0) {
			types = new Class[args.length];
			for (int i = 0; i < args.length; i++) {
				if (args[i] != null) {
					types[i] = args[i].getClass();
				}
			}
		}
		return newOsgiInstance(obj, className, types, args);
	}

	public static Object newOsgiInstance(Object obj, String className, Class<?>[] types, Object[] args) {
		try {
			Class<?> instanceClass;
			if (obj instanceof Class) {
				instanceClass = (Class<?>) obj;
			} else {
				instanceClass = obj.getClass().getClassLoader().loadClass(className);
			}

			if (args == null || args.length == 0) {
				return instanceClass.newInstance();
			} else {
				return instanceClass.getConstructor(types).newInstance(args);
			}

		} catch (Exception e) {
			logger.info("new Osgi Instance error:" + e.getMessage());
			// e.printStackTrace();
		}

		return null;
	}

	/**
	 * Get field declared in class loaded in OSGI bundle.
	 * 
	 * @param obj
	 * @param name
	 * @param isPrivate
	 * @return
	 */
	public static Object getOsgiField(Object obj, String name, boolean isPrivate) {
		try {

			if (obj == null) {
				return null;
			}
			Class<?> objClass;
			if (obj instanceof Class) {
				objClass = (Class<?>) obj;
				obj = null;
			} else if (obj instanceof String) {
				try {
					objClass = Class.forName((String) obj);
				} catch (ClassNotFoundException e) {
					logger.info(obj + " not found!");
					return null;
				}
				obj = null;
			} else {
				objClass = obj.getClass();
			}

			Field field = seekOsgiField(objClass, name);
			if (field == null) {
				logger.info("get Osgi Field error,name: " + name + " not exits!");
				return null;
			}

			if (isPrivate) {
				field.setAccessible(true);
			}
			return field.get(obj);
		} catch (Exception e) {
			logger.info("get Osgi Field error,name: " + name + " " + e.getMessage());
			return null;
		}

	}

	/**
	 * Set field declared in class loaded in OSGI bundle.
	 * 
	 * @param obj
	 * @param name
	 * @param value
	 * @param isPrivate
	 */
	public static void setOsgiField(Object obj, String name, Object value, boolean isPrivate) {
		try {

			if (obj == null) {
				return;
			}
			Class<?> objClass;
			if (obj instanceof Class) {
				objClass = (Class<?>) obj;
				obj = null;
			} else {
				objClass = obj.getClass();
			}

			Field field = seekOsgiField(objClass, name);
			if (field == null) {
				logger.info("get Osgi Field error,name: " + name + " not exits!");
				return;
			}

			if (isPrivate) {
				field.setAccessible(true);
			}

			field.set(obj, value);
		} catch (IllegalArgumentException | IllegalAccessException e) {
			logger.info("get Osgi Field error,name: " + name + " " + e.getMessage());
		}

	}

	private static Field seekOsgiField(Class<?> clazz, String name) {
		try {
			return clazz.getDeclaredField(name);
		} catch (Exception e) {
			if (clazz.getGenericSuperclass() != null) {
				// 查找父类
				return seekOsgiField(clazz.getSuperclass(), name);
			}
			logger.info("get Osgi Field error,name: " + name + " " + e.getMessage());
		}
		return null;
	}

	private static Map<Class<?>, Class<?>> baseTypeTransfor = new HashMap<Class<?>, Class<?>>();
	static {
		baseTypeTransfor.put(Integer.class, int.class);
		baseTypeTransfor.put(int.class, Integer.class);
		baseTypeTransfor.put(Boolean.class, boolean.class);
		baseTypeTransfor.put(boolean.class, Boolean.class);
		baseTypeTransfor.put(Long.class, long.class);
		baseTypeTransfor.put(long.class, Long.class);
		baseTypeTransfor.put(Double.class, double.class);
		baseTypeTransfor.put(double.class, Double.class);
		baseTypeTransfor.put(Float.class, float.class);
		baseTypeTransfor.put(float.class, Float.class);
		baseTypeTransfor.put(Character.class, char.class);
		baseTypeTransfor.put(char.class, Character.class);
		baseTypeTransfor.put(Byte.class, byte.class);
		baseTypeTransfor.put(byte.class, Byte.class);
		baseTypeTransfor.put(Short.class, short.class);
		baseTypeTransfor.put(short.class, Short.class);
	}

	/**
	 * Seek method in class loaded in OSGI bundle.
	 * 
	 * @param clazz
	 * @param name
	 * @param argTypes
	 * @return
	 */
	private static Method seekOsgiMethod(Class<?> clazz, String name, Class<?>... argTypes) {
		try {
			return clazz.getDeclaredMethod(name, argTypes);
		} catch (NoSuchMethodException e) {
			if (argTypes.length > 0) {
				List<Method> listMethod = Arrays.asList(clazz.getDeclaredMethods()).stream()
						.filter(method -> method.getName().equals(name)).collect(Collectors.toList());
				if(listMethod.size() ==1 ){
					return listMethod.get(0);
				}
				for (Method method : listMethod) {
					Class<?>[] paramTypes = method.getParameterTypes();
					if (paramTypes != null && paramTypes.length == argTypes.length) {
						for (int i = 0; i < paramTypes.length; i++) {

							if (paramTypes[i].isArray() && argTypes[i].isArray()) {
								continue;
							}
							if (baseTypeTransfor.containsKey(paramTypes[i])
									&& !baseTypeTransfor.get(paramTypes[i]).equals(argTypes[i])) {
								logger.info("seek Osgi Method error ,paramTypes is not match ,Name:" + name + " ,error:"
										+ e.getMessage());
								return null;
							}
						}

						return method;
					}

				}
			}

			if (clazz.getGenericSuperclass() != null) {
				// 查找父类
				return seekOsgiMethod(clazz.getSuperclass(), name, argTypes);
			}
			logger.info("seek Osgi Method error ,Name:" + name + " ,error:" + e.getMessage());
			return null;
		}
	}

}
