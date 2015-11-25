package org.joverseer.tools.ordercheckerIntegration;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * A set of static utility methods for accessing methods and fields (private and
 * public) on a given object using reflection.
 * 
 * @author Marios Skounakis
 */

// TODO move outside the orderchecker package?
public class ReflectionUtils {
	public static Object invokeMethod(Object obj, String methodName, Object[] args) throws Exception {
		Class<?> clazz = obj.getClass();
		Exception exception = null;

		for (Method m : clazz.getDeclaredMethods()) {
			if (m.getName().equals(methodName) && m.getParameterTypes().length == args.length) {
				try {
					m.setAccessible(true);
					return m.invoke(obj, args);
				} catch (Exception exc) {
					exception = exc;
				}
				;
			}
		}
		if (exception != null) {
			throw exception;
		}
		throw new Exception("Method " + methodName + " not found for class " + obj.getClass().getName());
	}

	@SuppressWarnings("unchecked")
	public static Object invokeStaticMethod(Class clazz, String methodName, Object[] args) throws Exception {
		Class[] argClasses = new Class[args.length];
		for (int i = 0; i < args.length; i++) {
			argClasses[i] = args[i].getClass();
		}
		Method m = clazz.getMethod(methodName, argClasses);
		return m.invoke(null, args);
	}

	public static Object retrieveField(Object obj, String fieldName) throws Exception {
		Class<?> clazz = obj.getClass();

		Field f = clazz.getDeclaredField(fieldName);
		f.setAccessible(true);
		return f.get(obj);

	}

	public static void assignField(Object obj, String fieldName, Object value) throws Exception {
		Class<?> clazz = obj.getClass();
		Field f = clazz.getDeclaredField(fieldName);
		f.setAccessible(true);
		f.set(obj, value);
	}

	public static Object instantiateObject(String className, Object[] args) throws Exception {
		Class<?> clazz = Class.forName(className);
		Exception exception = null;
		for (Constructor<?> c : clazz.getDeclaredConstructors()) {
			if (c.getParameterTypes().length == args.length) {
				try {
					c.setAccessible(true);
					return c.newInstance(args);
				} catch (Exception exc) {
					exception = exc;
				}
				;
			}
		}
		if (exception != null) {
			throw exception;
		}

		throw new Exception("Constructor with appropriate args not found for class " + className);
	}

	public static void main(String[] args) throws Exception {
		Object a = instantiateObject("com.middleearthgames.orderchecker.Artifact", new Object[] { (int) 100, "artiname", 25, 1, "asdasda" });
		System.out.println(retrieveField(a, "artifact"));
		System.out.println(invokeMethod(a, "getName", new Object[] {}));
	}
}
