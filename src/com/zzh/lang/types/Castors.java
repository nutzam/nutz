package com.zzh.lang.types;

import java.io.File;
import java.io.FileFilter;
import java.lang.reflect.Array;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import com.zzh.lang.Files;
import com.zzh.lang.Lang;
import com.zzh.lang.Mirror;

public class Castors {

	private static final CastorSetting defaultSetting = new CastorSetting() {
	};

	private static Map<Integer, Castors> castorsMap = new HashMap<Integer, Castors>();

	public static Castors me() {
		return me(defaultSetting);
	}

	public static Castors me(CastorSetting setting) {
		if (null == setting)
			setting = defaultSetting;
		Castors cc = castorsMap.get(setting.getId());
		if (null == cc) {
			synchronized (castorsMap) {
				cc = castorsMap.get(setting.getId());
				if (null == cc) {
					cc = new Castors(setting);
					castorsMap.put(setting.getId(), cc);
				}
			}
		}
		return cc;
	}

	private Castors(CastorSetting setting) {
		if (null == setting)
			setting = defaultSetting;
		map = new HashMap<String, Map<String, Castor<?, ?>>>();
		String defaultPath = Castor.class.getName().toLowerCase().replace('.', '/');
		buildCastors(defaultPath, setting);
		if (null != setting.extraCastorPaths())
			for (String path : setting.extraCastorPaths()) {
				buildCastors(path, setting);
			}
	}

	private void buildCastors(String path, CastorSetting setting) {
		String[] classNames = findCastorsInClassPath(path);
		if (null == classNames) {
			classNames = findCastorsInJar(path);
		}
		if (null != classNames)
			for (String className : classNames) {
				try {
					Class<?> klass = Class.forName(className);
					Castor<?, ?> castor = (Castor<?, ?>) klass.newInstance();
					Map<String, Castor<?, ?>> map2 = map.get(castor.getFromClass().getName());
					if (null == map2) {
						map2 = new HashMap<String, Castor<?, ?>>();
						map.put(castor.getFromClass().getName(), map2);
					}
					if (!map2.containsKey(castor.getToClass())) {
						map2.put(castor.getToClass().getName(), castor);
						setting.setup(castor);
					}
				} catch (Exception e) {
				}
			}
	}

	// find the jar file where contains Castors
	private static String[] findCastorsInJar(String path) {
		String[] classNames = null;
		File f = Files.findFile(path);
		String fpath = f.getAbsolutePath();
		int posBegin = fpath.indexOf("file:")+4;
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
						classNames[i] = ph.substring(0, ph.lastIndexOf('.')).replace('/', '.');
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
			@Override
			public boolean accept(File pathname) {
				return pathname.getName().endsWith(".class");
			}
		});
		String[] classNames = null;
		if (null != files) {
			classNames = new String[files.length];
			for (int i = 0; i < files.length; i++) {
				String ph = files[i].getAbsolutePath();
				classNames[i] = ph.substring(pos, ph.lastIndexOf('.')).replace('/', '.');
			}
		}
		return classNames;
	}

	/**
	 * First index is "from" (source) The second index is "to" (target)
	 */
	private Map<String, Map<String, Castor<?, ?>>> map;

	@SuppressWarnings("unchecked")
	public <F, T> T cast(Object src, Class<F> fromType, Class<T> toType)
			throws FailToCastObjectException {
		if (null == src)
			return null;
		if (fromType == toType)
			return (T) src;
		if (fromType.getName().equals(toType.getName()))
			return (T) src;
		if (toType.isAssignableFrom(fromType))
			return (T) src;
		// Array => Collection
		if (fromType.isArray() && Collection.class.isAssignableFrom(toType)) {
			if (Modifier.isAbstract(toType.getModifiers())) {
				Collection coll = null;
				if (toType.isAssignableFrom(ArrayList.class)) {
					coll = new ArrayList(Array.getLength(src));
				} else {
					throw new RuntimeException(String.format(
							"Castors don't know how to implement '%s'", toType.getName()));
				}
				for (int i = 0; i < Array.getLength(src); i++) {
					coll.add(Array.get(src, i));
				}
				return (T) coll;
			}
		}
		// Collection => Array
		if (Collection.class.isAssignableFrom(fromType) && toType.isArray()) {
			Collection coll = (Collection) src;
			Class<?> eleType = toType.getComponentType();
			Object ary = Array.newInstance(eleType, coll.size());
			int index = 0;
			for (Iterator it = coll.iterator(); it.hasNext();) {
				Array.set(ary, index++, castTo(it.next(), eleType));
			}
			return (T) ary;
		}
		Mirror<?> from = Mirror.me(fromType);
		// Use language built-in cases
		if (from.canCastToDirectly(toType)) {
			return (T) src;
		}
		Mirror<T> to = Mirror.me(toType);
		if (from.isNumber()) {
			// Convert Number to boolean
			if (to.isBoolean()) {
				return (T) new Boolean(src.toString().charAt(0) == '0' ? false : true);
			}
			// Convert Number to String
			if (to.isString()) {
				return (T) String.valueOf(src);
			}
			// Convert Number to Number
			if (to.isNumber()) {
				try {
					return (T) to.getWrpperClass().getConstructor(String.class).newInstance(
							String.valueOf(src));
				} catch (Exception e) {
					throw Lang.wrapThrow(e);
				}
			}
		}
		if (from.isBoolean()) {
			// Convert Boolean to String
			if (to.isString()) {
				return (T) src.toString();
			}
			// Convert Boolean to number
			if (to.isPrimitiveNumber()) {
				return (T) new Integer((Boolean) src ? 1 : 0);
			}
		}
		if (from.isStringLike()) {
			if (to.isNumber()) {
				try {
					return (T) to.getWrpperClass().getConstructor(String.class).newInstance(
							src.toString());
				} catch (Exception e) {
					throw Lang.wrapThrow(e);
				}
			}
		}
		// Customized
		try {
			Map<String, Castor<?, ?>> m2 = map.get(fromType.getName());
			Castor c = m2.get(toType.getName());
			return (T) c.cast(src);
		} catch (Exception e) {
			if (String.class.getName().equals(toType.getName())) {
				try {
					return (T) src.toString();
				} catch (Exception e1) {
					throw new FailToCastObjectException(String.format(
							"Fail to cast type from <%s> to <%s> for {%s} because '%s'", fromType
									.getName(), toType.getName(), src, e.getMessage()));
				}
			} else
				throw new FailToCastObjectException(String.format(
						"Fail to cast type from <%s> to <%s> for {%s} because '%s'", fromType
								.getName(), toType.getName(), src, e.getMessage()));
		}
	}

	public <T> T castTo(Object src, Class<T> toType) throws FailToCastObjectException {
		return cast(src, src.getClass(), toType);
	}

	public String castToString(Object src, Class<?> fromType) {
		try {
			if (src instanceof CharSequence)
				return src.toString();
			return cast(src, fromType, String.class);
		} catch (FailToCastObjectException e) {
			return src.toString();
		}
	}

	public String castToString(Object src) {
		return castToString(src, src.getClass());
	}
}
