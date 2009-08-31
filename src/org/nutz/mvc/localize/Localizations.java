package org.nutz.mvc.localize;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;

import org.nutz.lang.Lang;
import org.nutz.lang.Strings;
import org.nutz.lang.segment.MultiLineProperties;

public class Localizations {

	private Localizations(File dir, final String suffix) {
		File[] list = dir.listFiles(new FileFilter() {
			public boolean accept(File f) {
				if (!f.isFile())
					return false;
				if (Strings.isBlank(suffix))
					return true;
				return f.getName().endsWith(suffix);
			}
		});
		lzs = new HashMap<String, Map<String, String>>();
		try {
			for (File f : list) {
				String lzName = f.getName().substring(0, f.getName().length() - suffix.length());
				MultiLineProperties lz = new MultiLineProperties(new InputStreamReader(new FileInputStream(f), "UTF-8"));
				lzs.put(lzName, lz);
			}
		} catch (Exception e) {
			throw Lang.wrapThrow(e);
		}
	}

	public static void init(ServletContext context, File dir, String suffix) {
		Localizations lzs = new Localizations(dir, suffix);
		context.setAttribute("nutz.mvc.locals", lzs);
	}

	private static Localizations me(ServletContext context) {
		return (Localizations) context.getAttribute("nutz.mvc.locals");
	}

	private Map<String, Map<String, String>> lzs;

	public Map<String, String> getDefault() {
		return get("default");
	}

	public Map<String, String> get(String localName) {
		return lzs.get(localName);
	}

	/*---------------------------------------------------------------------------*/
	public static void setLocalization(HttpSession session, String localName) {
		session.setAttribute("nutz.mvc.local", localName);
	}

	public static Map<String, String> getLocalization(HttpSession session) {
		Object localName = session.getAttribute("nutz.mvc.local");
		if (null != localName) {
			Localizations lzs = Localizations.me(session.getServletContext());
			if (null != lzs) {
				Map<String, String> re = lzs.get(localName.toString());
				if (null == re)
					return lzs.getDefault();
				return re;
			}
		}
		return null;
	}

	public static String[] available(ServletContext context) {
		Localizations me = Localizations.me(context);
		if (null == me)
			return new String[0];
		Map<String, Map<String, String>> lzs = me.lzs;
		return lzs.keySet().toArray(new String[lzs.size()]);
	}
}
