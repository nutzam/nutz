package org.nutz.mvc.init;

import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.nutz.lang.Lang;
import org.nutz.lang.segment.MultiLineProperties;
import org.nutz.mvc.MessageLoader;
import org.nutz.mvc.Mvcs;
import org.nutz.resource.NutResource;
import org.nutz.resource.ResourceScan;

public class DefaultMessageLoader implements MessageLoader {

	public Map<String, Map<String, String>> load(ResourceScan scan, String refer) {
		Map<String, Map<String, String>> re = new HashMap<String, Map<String, String>>();
		List<NutResource> allnrs = scan.list(refer, "^.+[.]properties$");
		// 求取路径的最大长度
		int max = 0;
		for (NutResource nr : allnrs) {
			String[] nms = nr.getName().split("[\\\\/]");
			max = Math.max(max, nms.length);
		}

		// 根据第二级目录，编制列表
		Map<String, List<NutResource>> map = new HashMap<String, List<NutResource>>();
		for (NutResource nr : allnrs) {
			String[] nms = nr.getName().split("[\\\\/]");
			// 如果不是最大长度，则一定是默认字符串
			String langType = nms.length < max ? Mvcs.DEFAULT_MSGS : nms[max - 2];
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
			for (String langType : map.keySet()) {
				List<NutResource> nrs = map.get(langType);
				for (NutResource nr : nrs) {
					MultiLineProperties p = new MultiLineProperties();
					Reader r = new InputStreamReader(nr.getInputStream(), "UTF-8");
					p.load(r);
					r.close();
					Map<String, String> langs = re.get(langType);
					if (null == langs) {
						re.put(langType, p);
					} else {
						langs.putAll(p);
					}
				}
			}
		}
		catch (Exception e) {
			throw Lang.wrapThrow(e);
		}
		// 返回结果
		return re;
	}

}
