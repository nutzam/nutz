package org.nutz.ioc;

/**
 * Ioc 容器接口
 * 
 * @author zozoh(zozohtnt@gmail.com)
 */
public interface Ioc {

	/**
	 * 从容器中获取一个对象。同时会触发对象的 fetch 事件。如果第一次构建对象 则会先触发对象 create 事件
	 * 
	 * @param <T>
	 * @param type
	 *            对象的类型，如果为 null，在对象的注入配置中，比如声明对象的类型 <br>
	 *            如果不为null对象注入配置的类型优先
	 * @param name
	 *            对象的名称
	 * @return 对象本身
	 */
	<T> T get(Class<T> type, String name) throws IocException;

	/**
	 * @param name
	 *            对象名
	 * @return 是否存在某一特定对象
	 */
	boolean has(String name) throws IocException;

	/**
	 * @return 所有在容器中定义了的对象名称列表。
	 */
	String[] getName();

	/**
	 * 将容器恢复成初始创建状态，所有的缓存都将被清空
	 */
	void reset();

	/**
	 * 将容器注销，触发对象的 depose 事件
	 */
	void depose();

}
