package org.nutz.app;

import java.io.IOException;
import java.io.Reader;
import java.util.Arrays;
import java.util.Map;
import java.util.TreeMap;

import org.nutz.json.Json;
import org.nutz.lang.Lang;
import org.nutz.lang.Streams;
import org.nutz.lang.Strings;

public class Help {

	public static void main(String[] args) throws IOException {
		sortMap("C:/eclipse/workspaces/zozoh/nutz.mole/bin/localization/mole.txt");
	}

	@SuppressWarnings("unchecked")
	private static void sortMap(String path) throws IOException {
		Reader r = Streams.fileInr(path);
		Map<String, String> map = (Map<String, String>) Json.fromJson(r);
		r.close();
		String[] keys = map.keySet().toArray(new String[map.size()]);
		Arrays.sort(keys);
		Map<String, String> fm = new TreeMap<String, String>();
		for (String key : keys) {
			fm.put(key, map.get(key));
		}
		System.out.println(Json.toJson(fm));
	}

	static int findIndex(String key, String[] refer) {
		for (int i = 0; i < refer.length; i++)
			if (refer[i].startsWith(key))
				return i;
		return -1;
	}

	static String[] readLines(String path) {
		String cn = Lang.readAll(Streams.fileInr(path));
		String[] lines = Strings.splitIgnoreBlank(cn, "[\n]");
		return lines;
	}

}
