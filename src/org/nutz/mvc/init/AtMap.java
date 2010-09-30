package org.nutz.mvc.init;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Pattern;

import org.nutz.lang.Strings;
import org.nutz.lang.meta.Pair;

public class AtMap {

	private Map<String, String> ats;

	public AtMap() {
		ats = new HashMap<String, String>();
	}

	public AtMap add(String key, String actionPath) {
		ats.put(Strings.trim(key), Strings.trim(actionPath));
		return this;
	}

	public Set<String> keys() {
		return ats.keySet();
	}

	public int size() {
		return ats.size();
	}

	public String get(String key) {
		return ats.get(key);
	}

	public AtMap clear() {
		ats.clear();
		return this;
	}

	public List<Pair<String>> getList(String... regexs) {
		List<Pair<String>> list = new ArrayList<Pair<String>>(ats.size());
		Set<Entry<String, String>> ens = ats.entrySet();
		for (Entry<String, String> en : ens) {
			if (null == regexs || regexs.length == 0)
				list.add(new Pair<String>(en.getKey(), en.getValue()));
			else {
				for (String regex : regexs)
					if (Pattern.matches(regex, en.getKey())) {
						list.add(new Pair<String>(en.getKey(), en.getValue()));
						break;
					}
			}
		}
		return list;
	}

}
