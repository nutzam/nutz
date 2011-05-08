package org.nutz.ioc.impl;

import java.io.IOException;
import java.util.List;
import java.util.Properties;

import org.nutz.lang.Lang;
import org.nutz.lang.Strings;
import org.nutz.lang.util.MultiLineProperties;
import org.nutz.resource.NutResource;
import org.nutz.resource.Scans;

/**
 * 代理Properties文件,以便直接在Ioc配置文件中使用
 * 
 * @author wendal(wendal1985@gmail.com)
 * @author zozoh(zozohtnt@gmail.com)
 * 
 * @since 1.b.37
 */
public class PropertiesProxy {

	// 是否为UTF8格式的Properties文件
	private boolean utf8;

	private MultiLineProperties mp;

	public PropertiesProxy() {
		this(true);
	}

	public PropertiesProxy(boolean utf8) {
		this.utf8 = utf8;
	}

	/**
	 * 加载指定文件/文件夹的Properties文件,合并成一个Properties对象
	 * <p>
	 * <b style=color:red>如果有重复的key,请务必注意加载的顺序!!<b/>
	 * 
	 * @param paths
	 *            需要加载的Properties文件路径
	 */
	public void setPaths(String... paths) {
		mp = new MultiLineProperties();
		List<NutResource> list = Scans.me().loadResource("^.+[.]properties$", paths);
		try {
			if (utf8)
				for (NutResource nr : list)
					mp.load(nr.getReader());
			else {
				Properties p = new Properties();
				for (NutResource nr : list) {
					p.load(nr.getInputStream());
				}
				mp.putAll(p);
			}
		}
		catch (IOException e) {
			throw Lang.wrapThrow(e);
		}
	}

	public String get(String key) {
		return mp.get(key);
	}

	public String get(String key, String defaultValue) {
		return Strings.sNull(mp.get(key), defaultValue);
	}
}
