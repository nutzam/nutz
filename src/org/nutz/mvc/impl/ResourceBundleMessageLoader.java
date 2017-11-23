package org.nutz.mvc.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.nutz.lang.util.NutMap;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.MessageLoader;
import org.nutz.mvc.Mvcs;
import org.nutz.resource.NutResource;
import org.nutz.resource.Scans;

/**
 * 使用类似于java.util.ResourceBundle的规则加载本地化数据
 * @author wendal(wendal1985@gmail.com)
 *
 */
public class ResourceBundleMessageLoader implements MessageLoader {
	
	private static final Log log = Logs.get();

	@Override
	public Map<String, Map<String, Object>> load(String refer) {
        Map<String, Map<String, Object>> re = new HashMap<String, Map<String, Object>>();
        re.put(Mvcs.DEFAULT_MSGS, new NutMap());
        List<NutResource> allnrs = Scans.me().scan(refer, "^.+[.]properties$");
        if (log.isDebugEnabled())
            log.debugf("Load Messages in %s resource : [%s]", allnrs.size(), allnrs);
        for (NutResource nr : allnrs) {
			try {
				String name = nr.getName();
				if (name.contains("/")) {
					name = name.substring(name.lastIndexOf('/') + 1);
				}
				if (name.contains("\\")) {
					name = name.substring(name.lastIndexOf('\\') + 1);
				}
				name = name.substring(0, name.length() - ".properties".length());
				String langType = Mvcs.DEFAULT_MSGS;
				if (name.contains("_")) {
					langType = name.substring(name.indexOf('_')+1);
				}
				Properties properties = new Properties();
				properties.load(nr.getInputStream());
				NutMap msgs = (NutMap) re.get(langType);
				if (msgs == null) {
					if (log.isDebugEnabled()) {
						log.debug("add Message Locale : " + langType);
					}
					msgs = new NutMap();
					re.put(langType, msgs);
				}
				for (Map.Entry<Object, Object> en : properties.entrySet()) {
					msgs.put(String.valueOf(en.getKey()), String.valueOf(en.getValue()));
				}
			} catch (Exception e) {
				throw new RuntimeException("error when reading " + nr.getName(), e);
			}
		}
		return re;
	}

}
