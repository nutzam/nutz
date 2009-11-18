package org.nutz.castor;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.nutz.castor.castor.Array2Array;
import org.nutz.lang.Mirror;
import org.nutz.lang.TypeExtractor;

/**
 * 一个创建 Castor 的工厂类。它的使用方式是：
 * 
 * <pre>
 * Castors.me().cast(obj, fromType, toType);
 * </pre>
 * 
 * 
 * @author zozoh(zozohtnt@gmail.com)
 * @author Wendal(wendal1985@gmail.com)
 */
public class Castors {

	private static Castors one;
	private static TypeExtractor typeExtractor = null;
	private static List<Class<?>> castorPaths = null;
	private static Object castorSetting;

	public static synchronized Castors resetSetting(Object setting) {
		return setSetting(new CastorSetting());
	}

	public static synchronized Castors setSetting(Object setting) {
		if (setting != null) {
			castorSetting = setting;
			one = new Castors(setting);
		}
		return me();
	}

	public static synchronized Castors setCastorPaths(List<Class<?>> paths) {
		castorPaths = paths;
		return setSetting(castorSetting);
	}

	public static synchronized Castors resetCastorPaths() {
		List<Class<?>> list = new ArrayList<Class<?>>();
		list.add(Array2Array.class);
		return setCastorPaths(list);
	}

	public static synchronized Castors addCastorPaths(Class<?>... paths) {
		if (null != paths) {
			for (Class<?> path : paths)
				castorPaths.add(path);
		}
		return setSetting(castorSetting);
	}

	public static synchronized Castors setTypeExtractor(TypeExtractor te) {
		typeExtractor = te;
		return me();
	}

	public static Castors me() {
		if (null == one)
			synchronized (Castors.class) {
				if (null == one)
					one = new Castors(castorSetting);
			}
		return one;
	}

	private Castors(Object setting) {
		if (null == setting)
			setting = new CastorSetting();
		// make setting map
		HashMap<Class<?>, Method> settingMap = new HashMap<Class<?>, Method>();
		for (Method m1 : setting.getClass().getMethods()) {
			Class<?>[] pts = m1.getParameterTypes();
			if (pts.length == 1 && Castor.class.isAssignableFrom(pts[0])) {
				settingMap.put(pts[0], m1);
			}
		}
		// build castors
		this.map = new HashMap<String, Map<String, Castor<?, ?>>>();
		if (null == castorPaths) {
			castorPaths = new ArrayList<Class<?>>();
			castorPaths.add(Array2Array.class);
		}
		for (Iterator<Class<?>> it = castorPaths.iterator(); it.hasNext();) {
			Class<?> baseClass = it.next();
			if (baseClass == null)
				continue;
			List<Class<?>> list  = Util.scanClass(baseClass);
			if (null == list)
				continue;
			for (Class<?> klass : list) {
				try {
					if (Modifier.isAbstract(klass.getModifiers()))
						continue;
					Castor<?, ?> castor =  (Castor<?, ?>) klass.newInstance();
					Map<String, Castor<?, ?>> map2 = this.map.get(castor.getFromClass().getName());
					if (null == map2) {
						map2 = new HashMap<String, Castor<?, ?>>();
						this.map.put(castor.getFromClass().getName(), map2);
					}
					if (!map2.containsKey(castor.getToClass().getName())) {
						Method m = settingMap.get(castor.getClass());
						if (null == m) {
							for (Iterator<Class<?>> mit = settingMap.keySet().iterator(); mit
									.hasNext();) {
								Class<?> cc = mit.next();
								if (cc.isAssignableFrom(klass)) {
									m = settingMap.get(cc);
									break;
								}
							}
						}
						if (null != m)
							m.invoke(castorSetting, castor);
						map2.put(castor.getToClass().getName(), castor);
					}
				} catch (Throwable e) {
					System.err.println(String.format("Fail to create castor [%s] because: %s",
							klass, e.getMessage()));
				}
			}
		}
	}

	/**
	 * First index is "from" (source) The second index is "to" (target)
	 */
	private Map<String, Map<String, Castor<?, ?>>> map;

	@SuppressWarnings("unchecked")
	public <F, T> T cast(Object src, Class<F> fromType, Class<T> toType, String... args)
			throws FailToCastObjectException {
		if (null == src) {
			if (toType.isPrimitive())
				return cast(0, int.class, toType);
			return null;
		}
		if (fromType == toType || toType == null || fromType == null)
			return (T) src;
		if (fromType.getName().equals(toType.getName()))
			return (T) src;
		if (toType.isAssignableFrom(fromType))
			return (T) src;
		Mirror<?> from = Mirror.me(fromType, typeExtractor);
		if (from.canCastToDirectly(toType)) // Use language built-in cases
			return (T) src;
		Mirror<T> to = Mirror.me(toType, typeExtractor);

		Castor c = null;
		Class<?>[] fets = from.extractTypes();
		Class<?>[] tets = to.extractTypes();
		for (Class<?> ft : fets) {
			Map<String, Castor<?, ?>> m2 = map.get(ft.getName());
			if (null != m2)
				for (Class<?> tt : tets) {
					c = m2.get(tt.getName());
					if (null != c)
						break;
				}
			if (null != c)
				break;
		}
		if (null == c)
			throw new FailToCastObjectException(String.format(
					"Can not find castor for '%s'=>'%s' because:\n%s", fromType.getName(), toType
							.getName(), "Fail to find matched castor"));
		try {
			return (T) c.cast(src, toType, args);
		} catch (FailToCastObjectException e) {
			throw e;
		} catch (Exception e) {
			throw new FailToCastObjectException(String.format(
					"Fail to cast from <%s> to <%s> for {%s} because:\n%s:%s", fromType.getName(),
					toType.getName(), src, e.getClass().getSimpleName(), e.getMessage()));
		}
	}

	public <T> T castTo(Object src, Class<T> toType) throws FailToCastObjectException {
		return cast(src, null == src ? null : src.getClass(), toType);
	}

	public String castToString(Object src) {
		try {
			return castTo(src, String.class);
		} catch (FailToCastObjectException e) {
			return String.valueOf(src);
		}
	}
}
