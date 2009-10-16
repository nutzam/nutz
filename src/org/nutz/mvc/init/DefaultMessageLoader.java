package org.nutz.mvc.init;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Map.Entry;

import org.nutz.lang.Files;
import org.nutz.lang.Lang;
import org.nutz.lang.Streams;
import org.nutz.mvc.MessageLoader;
import org.nutz.mvc.Mvcs;

public class DefaultMessageLoader implements MessageLoader {

	private static final String MSG_SUFFIX = ".properties";
	private File dir;

	public DefaultMessageLoader(String path) {
		dir = Files.findFile(path);
		if (null == dir || !dir.isDirectory())
			throw Lang.makeThrow("'%s' is not a directory", path);
	}

	public Map<String, Map<String, String>> load() {
		Map<String, Map<String, String>> msgss = new HashMap<String, Map<String, String>>();
		// Load default
		String key = Mvcs.DEFAULT_MSGS;
		Map<String, String> msgs = _load(dir);
		if (null != msgs)
			msgss.put(key, msgs);
		// Local for each locale languange
		File[] dirs = dir.listFiles(new FileFilter() {
			public boolean accept(File f) {
				if (f.isDirectory())
					if (!f.getName().startsWith("."))
						return true;
				return false;
			}
		});
		for (File d : dirs) {
			key = d.getName();
			msgs = _load(d);
			if (null != msgs)
				msgss.put(key, msgs);
		}
		// return it
		return msgss.size() == 0 ? null : msgss;
	}

	private static Map<String, String> _load(File dir) {
		if (null == dir || !dir.isDirectory())
			return null;
		Map<String, String> msgs = new HashMap<String, String>();
		File[] files = dir.listFiles(new FileFilter() {
			public boolean accept(File f) {
				if (f.isFile())
					if (f.getName().endsWith(MSG_SUFFIX))
						return true;
				return false;
			}
		});
		for (File f : files) {
			Properties p = new Properties();
			Reader reader = Streams.fileInr(f);
			try {
				p.load(reader);
			} catch (IOException e) {
				throw Lang.wrapThrow(e);
			} finally {
				try {
					reader.close();
				} catch (IOException e) {
					throw Lang.wrapThrow(e);
				}
			}
			for (Entry<?, ?> en : p.entrySet())
				msgs.put(en.getKey().toString(), en.getValue().toString());
		}
		return msgs.size() == 0 ? null : msgs;
	}

}
