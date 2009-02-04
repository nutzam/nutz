package com.zzh.castor;

import java.io.File;
import java.io.FileFilter;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import com.zzh.lang.Files;
import com.zzh.lang.Lang;
import com.zzh.lang.Mirror;
import com.zzh.lang.TypeExtractor;

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

	public Castors() {
		this(null);
	}

	public Castors(CastorSetting setting) {
		if (null == setting)
			setting = defaultSetting;
		map = new HashMap<String, Map<String, Castor<?, ?>>>();
		typeExtractor = setting.getTypeExtractor();
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
				Castor<?, ?> castor = null;
				try {
					Class<?> klass = Class.forName(className);
					if (Modifier.isAbstract(klass.getModifiers()))
						continue;
					castor = (Castor<?, ?>) klass.newInstance();
					castor.castors = this;
				} catch (Throwable e) {
					System.err.println(String.format("Fail to create castor '%s' because: %s",
							className, e.getMessage()));
					continue;
				}
				Map<String, Castor<?, ?>> map2 = map.get(castor.getFromClass().getName());
				if (null == map2) {
					map2 = new HashMap<String, Castor<?, ?>>();
					map.put(castor.getFromClass().getName(), map2);
				}
				if (!map2.containsKey(castor.getToClass())) {
					map2.put(castor.getToClass().getName(), castor);
					setting.setup(castor);
				}

			}
	}

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
				classNames[i] = ph.substring(pos, ph.lastIndexOf('.')).replaceAll("[\\\\|/]", ".");
			}
		}
		return classNames;
	}

	/**
	 * First index is "from" (source) The second index is "to" (target)
	 */
	private Map<String, Map<String, Castor<?, ?>>> map;
	private TypeExtractor typeExtractor;

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
		Mirror<?> from = Mirror.me(fromType, typeExtractor);
		if (from.canCastToDirectly(toType)) // Use language built-in cases
			return (T) src;
		Mirror<T> to = Mirror.me(toType, typeExtractor);

		Map<String, Castor<?, ?>> m2 = map.get(fromType.getName());
		Castor c;
		if (null == m2) {// try to adjust "from" type
			c = findCastor(toType, to, map.get(from.extractType().getName()));
		} else {
			c = findCastor(toType, to, m2);
			if (null == c)
				c = findCastor(toType, to, map.get(from.extractType().getName()));
		}
		if (null == c) {
			if (from.is(String.class))
				try {
					return Mirror.me(toType).born(src);
				} catch (Exception e) {
					throw makeException(fromType, toType, "Fail to auto-born");
				}
			else if (to.is(String.class))
				return (T) src.toString();
			else {
				throw makeException(fromType, toType, "Fail to find matched castor");
			}
		}
		try {
			return (T) c.cast(src, toType);
		} catch (Exception e) {
			throw new FailToCastObjectException(String.format(
					"Fail to cast type from <%s> to <%s> for {%s} because '%s'",
					fromType.getName(), toType.getName(), src, e.getMessage()));
		}
	}

	private static <F, T> FailToCastObjectException makeException(Class<F> fromType,
			Class<T> toType, String reason) {
		return new FailToCastObjectException(String.format(
				"Can not find castor for '%s'=>'%s' for the reason: %s", fromType.getName(), toType
						.getName(), reason));
	}

	@SuppressWarnings("unchecked")
	private static <T> Castor findCastor(Class<T> toType, Mirror<T> to, Map<String, Castor<?, ?>> m2) {
		Castor c = m2.get(toType.getName());
		if (null == c) // try to adjust "to" type
			c = m2.get(to.extractType().getName());
		return c;
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
