package com.zzh.mvc.localize;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;

import com.zzh.lang.Lang;

public class Localizations {

	public Localizations(File dir, final String suffix) {
		File[] list = dir.listFiles(new FileFilter() {
			@Override
			public boolean accept(File f) {
				if(!f.isFile())
					return false;
				if(null==suffix)
					return true;
				return f.getName().endsWith(suffix);
			}
		});
		lzs = new HashMap<String, Map<String, String>>();
		try {
			for (File f : list) {
				String lzName = f.getName().substring(0, f.getName().length() - suffix.length());
				Map<String, String> lz = new HashMap<String, String>();
				Properties p = new Properties();
				p.load(new InputStreamReader(new FileInputStream(f)));
				for (Iterator<?> it = p.keySet().iterator(); it.hasNext();) {
					Object key = it.next();
					Object value = p.get(key);
					lz.put(key.toString(), value.toString());
				}
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
}
