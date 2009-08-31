package org.nutz.castor;

import java.io.File;
import java.io.FileFilter;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.nutz.lang.Files;
import org.nutz.lang.Lang;
import org.nutz.lang.Mirror;
import org.nutz.lang.TypeExtractor;

public class Castors {

	// find the jar file where contains Castors
	private static String[] findCastorsInJar(String path) {
		String[] classNames = null;
		File f = Files.findFile(path);
		String fpath = f.getAbsolutePath();
		int posBegin = fpath.indexOf("file:") + 4;
		int posEnd = fpath.lastIndexOf('!');
		if (posBegin > 0 && posEnd > 0) {
			String jarPath = fpath.substring(posBegin + 1, posEnd);
			try {
				ZipEntry[] entrys = Files.findEntryInZip(new ZipFile(jarPath), path.toLowerCase()
						+ "/\\w*.class");
				if (null != entrys) {
					classNames = new String[entrys.length];
					for (int i = 0; i < entrys.length; i++) {
						String ph = entrys[i].getName();
						classNames[i] = ph.substring(0, ph.lastIndexOf('.')).replaceAll("[\\\\|/]",
								".");
					}
				}
			} catch (Exception e) {
				throw Lang.wrapThrow(e);
			}
		}
		return classNames;
	}

	private static String[] findCastorsInClassPath(String path) {
		File dir = Files.findFile(path);
		int pos = dir.getAbsolutePath().length() - path.length();
		File[] files = dir.listFiles(new FileFilter() {
			public boolean accept(File pathname) {
				return pathname.getName().endsWith(".class");
			}
		});
		String[] classNames = null;
		if (null != files) {
			classNames = new String[files.length];
			for (int i = 0; i < files.length; i++) {
				String ph = files[i].getAbsolutePath();
				classNames[i] = ph.substring(pos, ph.lastIndexOf('.')).replaceAll("[\\\\|/]", ".");
			}
		}
		return classNames;
	}

	private static Castors one = new Castors(new CastorSetting());
	private static TypeExtractor typeExtractor = null;
	private static List<String> castorPaths = null;
	private static Object castorSetting;

	public static synchronized Castors resetSetting(Object setting) {
		return setSetting(new CastorSetting());
	}

	public static synchronized Castors setSetting(Object setting) {
		if (setting != null) {
			castorSetting = setting;
			one = new Castors(setting);
		}
		return one;
	}

	public static synchronized Castors setCastorPaths(List<String> paths) {
		castorPaths = paths;
		return setSetting(castorSetting);
	}

	public static synchronized Castors resetCastorPaths() {
		List<String> list = new ArrayList<String>();
		list.add(Castor.class.getName().toLowerCase().replace('.', '/'));
		return setCastorPaths(list);
	}

	public static synchronized Castors addCastorPaths(String... paths) {
		if (null != paths) {
			for (String path : paths)
				castorPaths.add(path);
		}
		return setSetting(castorSetting);
	}

	public static synchronized Castors setTypeExtractor(TypeExtractor te) {
		typeExtractor = te;
		return one;
	}

	public static Castors me() {
		return one;
	}

	private Castors(Object setting) {
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
			castorPaths = new ArrayList<String>();
			castorPaths.add(Castor.class.getName().toLowerCase().replace('.', '/'));
		}
		for (Iterator<String> it = castorPaths.iterator(); it.hasNext();) {
			String path = it.next();
			String[] classNames = findCastorsInClassPath(path);
			if (null == classNames) {
				classNames = findCastorsInJar(path);
			}
			if (null == classNames)
				continue;
			for (String className : classNames) {
				try {
					Castor<?, ?> castor = null;
					Class<?> klass = Class.forName(className);
					if (Modifier.isAbstract(klass.getModifiers()))
						continue;
					castor = (Castor<?, ?>) klass.newInstance();
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
							className, e.getMessage()));
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
					"Fail to cast type from <%s> to <%s> for {%s} because:\n%s",
					fromType.getName(), toType.getName(), src, e.getMessage()));
		}
	}

	public <T> T castTo(Object src, Class<T> toType) throws FailToCastObjectException {
		return cast(src, null == src ? null : src.getClass(), toType);
	}

	public String castToString(Object src) {
		try {
			return castTo(src, String.class);
		} catch (FailToCastObjectException e) {
			return src.toString();
		}
	}
}
