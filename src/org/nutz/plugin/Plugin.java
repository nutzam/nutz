package org.nutz.plugin;

/**
 * 插件 -- 一个通用的扩展点
 * <p>
 * 某些时候，你可能需要让你的程序在部署时才决定一个接口的实现。同 OSGI 不同，你的应用 其实并不在乎更换一个接口实现时，将服务停止，然后更换实现的
 * jar 包。所以你可能需要 一个更简单的插件体系。
 * <p>
 * 你可以实现这个接口，并在 CLASSPATH 中放置一个文本文件： "nutz-plugin.json"，内容类似：
 * 
 * <pre>
 * [
 * &quot;com.you.app.YourPluginClassName&quot;,
 * &quot;com.you.app.YourPluginClassName2&quot;,
 * ...
 * ]
 * </pre>
 * 
 * 通过这个 JSON 文本文件， 声明你的插件实现。
 * <p>
 * 你的调用代码，可以通过类似：
 * 
 * <pre>
 * MyInterface[] Plugins.get(MyInteface.class);
 * </pre>
 * 
 * 获得可用的一组接口的实现。然后你可以自行决定采用哪些实现。
 * 
 * 
 * @author wendal(wendal1985@gmail.com)
 * @author zozoh(zozohtnt@gmail.com)
 */
public interface Plugin {

	/**
	 * @return 当前插件是否能正常工作
	 */
	boolean canWork();

	/**
	 * @return 当前插件是为了实现哪一个接口
	 */
	Class<?> getWorkType();

	/**
	 * 插件的初始化方法
	 * 
	 * @throws Throwable
	 */
	void init() throws Throwable;

	/**
	 * 注销插件
	 * 
	 * @throws Throwable
	 */
	void depose() throws Throwable;
}
