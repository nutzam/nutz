package org.nutz.mvc.init;

import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.nutz.json.Json;
import org.nutz.lang.Lang;
import org.nutz.lang.Strings;
import org.nutz.lang.util.MultiLineProperties;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.MessageLoader;
import org.nutz.mvc.Mvcs;
import org.nutz.resource.NutResource;
import org.nutz.resource.Scans;

public class DefaultMessageLoader implements MessageLoader {

	private static final Log log = Logs.getLog(DefaultMessageLoader.class);

	public Map<String, Map<String, String>> load(String refer) {
		Map<String, Map<String, String>> re = new HashMap<String, Map<String, String>>();
		List<NutResource> allnrs = Scans.me().scan(refer, "^.+[.]properties$");
		for (NutResource nutResource : allnrs) {
			if (nutResource.getName().indexOf(refer) > -1)
				nutResource.setName(nutResource.getName().substring(refer.length() + 1));
		}
		if (log.isDebugEnabled())
			log.debugf("Load Messages in %s resource : [%s]", allnrs.size(), allnrs);
		// 求取路径的最大长度
		int max = 0;
		for (NutResource nr : allnrs) {
			String[] nms = nr.getName().split("[\\\\/]");
			max = Math.max(max, nms.length);
		}

		// 根据第二级目录，编制列表
		Map<String, List<NutResource>> map = new HashMap<String, List<NutResource>>();
		for (NutResource nr : allnrs) {
			String langType;
			String resName = nr.getName();
			if (resName.contains("/"))
				langType = resName.substring(0, resName.indexOf('/'));
			else if (resName.contains("\\"))
				langType = resName.substring(0, resName.indexOf('\\'));
			else
				langType = Mvcs.DEFAULT_MSGS;
			// 按语言类型编制
			List<NutResource> list = map.get(langType);
			if (null == list) {
				list = new ArrayList<NutResource>(10);
				map.put(langType, list);
			}
			list.add(nr);
		}
		// 根据语言的分类，依次构建字符串 Map
		try {
			for (Entry<String, List<NutResource>> entry : map.entrySet()) {
				List<NutResource> nrs = entry.getValue();
				for (NutResource nr : nrs) {
					MultiLineProperties p = new MultiLineProperties() {
						public String get(Object key) {
							return Strings.sNull(super.get(key), (String) key);
						}
					};
					Reader r = nr.getReader();
					p.load(r);
					r.close();
					Map<String, String> langs = re.get(entry.getKey());
					if (null == langs)
						re.put(entry.getKey(), p);
					else
						langs.putAll(p);
				}
			}
		}
		catch (Exception e) {
			throw Lang.wrapThrow(e);
		}
		if (log.isDebugEnabled())
			log.debugf("Message Loaded, size = %s", re.size());
		if (log.isTraceEnabled())
			log.tracef("Messages -->\n%s", Json.toJson(re));
		// 返回结果
		return re;
	}

}
