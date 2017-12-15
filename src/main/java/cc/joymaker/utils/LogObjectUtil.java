package cc.joymaker.utils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

public class LogObjectUtil {

	public static void info(Logger logger,String prefix, String... msg) {
		info(logger, '\t',prefix,  msg);
	}

	public static void info(Logger logger, char split, String prefix, String... msg) {
		String info = StringUtils.join(msg, split);
		logger.info(info);
	}

	public static void infoObj(Logger logger, char split, String prefix, Object obj) {
		Field[] fields = obj.getClass().getDeclaredFields();

		List<String> fieldsList = new ArrayList<>();
		for (Field f : fields) {
			Object value = null;
			try {
				value = getFieldValue(obj, f);
				if (value != null)
					fieldsList.add(value.toString());
			} catch (Exception ex) {

			}
		}
		
		String[] msgs = new String[fieldsList.size()];
		fieldsList.toArray(msgs);
		info(logger, split, prefix, msgs);
	}

	private static Object getFieldValue(Object o, Field f) throws IllegalArgumentException, IllegalAccessException,
			NoSuchMethodException, SecurityException, InvocationTargetException {
		String name = f.getName();
		if (f.isAccessible()) {
			return f.get(o);
		} else {
			String getterName = "get" + (name.charAt(0) + "").toUpperCase() + name.substring(1);
			Method getter = o.getClass().getMethod(getterName);
			return getter.invoke(o);
		}
	}
}
