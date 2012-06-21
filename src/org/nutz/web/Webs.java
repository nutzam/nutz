package org.nutz.web;

/**
 * 一些常量和帮助函数的集合
 * 
 * @author zozoh(zozohtnt@gmail.com)
 */
public abstract class Webs {

	/**
	 * 在会话中，表示当前用户的键
	 */
	public static final String ME = "me";

	/**
	 * 默认配置文件路径
	 */
	public static final String CONF_PATH = "web.properties";

	/**
	 * 封装所有错误
	 */
	public static abstract class Err {

		public static WebException create(String key) {
			return create(null, key, null);
		}

		public static WebException create(String key, Object reason) {
			return create(null, key, reason);
		}

		public static WebException create(Throwable e, String key, Object reason) {
			return new WebException(e).key(key).reason(reason);
		}

		public static WebException wrap(Throwable e) {
			if (e instanceof WebException)
				return (WebException) e;
			return new WebException(e).key(e.getClass().getName()).reason(e.toString());
		}

	}

}
