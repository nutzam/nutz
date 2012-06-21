package org.nutz.web;

import java.io.IOException;
import java.util.List;

import org.nutz.ioc.impl.PropertiesProxy;
import org.nutz.lang.Lang;
import org.nutz.lang.Streams;
import org.nutz.lang.Strings;
import org.nutz.lang.util.MultiLineProperties;

/**
 * 封装 web.properies 的读取，你的应用可以继承这个类，实现自己更专有的配置文件读取类
 * 
 * @author zozoh(zozohtnt@gmail.com)
 */
public class WebConfig {

	/**
	 * 配置文件的键名: 应用的根路径，比如 "~/workspace/git/danoo/strato/domain/ROOT"
	 */
	private static final String APP_ROOT = "app-root";

	/**
	 * 配置文件的键名: 应用监听的端口，比如 8080
	 */
	private static final String APP_PORT = "app-port";

	/**
	 * 配置文件的键名: 应用静态资源的地址前缀，比如 "http://localhost/strato"，或者 "/rs" 等
	 */
	private static final String APP_RS = "app-rs";

	/**
	 * 配置文件的键名: 应用的类路径，可多行
	 */
	private static final String APP_CLASSPATH = "app-classpath";

	/**
	 * 配置文件的键名: 应用的管理端口，比如 8081
	 */
	private static final String ADMIN_PORT = "admin-port";

	/**
	 * 配置文件的键名: 引入更多的配置文件
	 */
	private static final String MACRO_INCLUDE = "$include";

	/**
	 * 存放所有的属性
	 */
	protected PropertiesProxy pp;

	public String getAppRoot() {
		return pp.get(APP_ROOT);
	}

	public int getAppPort() {
		return pp.getInt(APP_PORT);
	}

	public String getAppRs() {
		return pp.get(APP_RS);
	}

	public String getAppClasspath() {
		return pp.get(APP_CLASSPATH);
	}

	public int getAdminPort() {
		return pp.getInt(ADMIN_PORT);
	}

	// ================================================= 一些通用方法

	public String get(String key) {
		return pp.get(key);
	}

	public String get(String key, String defaultValue) {
		return pp.get(key, defaultValue);
	}

	public int getInt(String key) {
		return pp.getInt(key);
	}

	public int getInt(String key, int dfval) {
		return pp.getInt(key, dfval);
	}

	public long getLong(String key) {
		return pp.getLong(key);
	}

	public long getLong(String key, long dfval) {
		return pp.getLong(key, dfval);
	}

	public String getTrim(String key) {
		return pp.getTrim(key);
	}

	public String getTrim(String key, String defaultValue) {
		return pp.getTrim(key, defaultValue);
	}

	public List<String> getKeys() {
		return pp.getKeys();
	}

	/**
	 * 在构造函数中解析配置文件
	 * 
	 * @param path
	 *            配置文件路径
	 */
	public WebConfig(String path) {
		// 开始解析
		this.pp = new PropertiesProxy();
		this.pp.setPaths(path);
		// 预处理键 : 引入其他的配置文件
		String str = this.pp.get(MACRO_INCLUDE);
		if (!Strings.isBlank(str)) {
			String[] ss = Strings.splitIgnoreBlank(str, "\n");
			try {
				for (String s : ss) {
					MultiLineProperties mp = new MultiLineProperties();
					mp.load(Streams.fileInr(s));
					for (String key : mp.keys())
						this.pp.put(key, mp.get(key));
				}
			}
			catch (IOException e) {
				throw Lang.wrapThrow(e);
			}
		}
	}

}
