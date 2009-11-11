package org.nutz.ioc;

/**
 * 进行对象装配的上下文环境。
 * 
 * @author zozoh(zozohtnt@gmail.com)
 */
public interface IocContext {

	/**
	 * 存储一个对象，根据对象的级别，各个实现类可以决定是否真的存储
	 * 
	 * @param level
	 *            对象的级别
	 * @param name
	 *            对象的名称
	 * @param obj
	 *            对象本身
	 * @return true 保存成功。 false 拒绝保存
	 */
	boolean save(String level, String name, ObjectProxy obj);

	/**
	 * 从上下文环境中删一个对象。实现类根据 level 信息来决定是否删除
	 * 
	 * @param level
	 *            对象的级别
	 * @param name
	 *            对象的名称
	 * 
	 * @return true 删除成功。 false 拒绝删除
	 */
	boolean remove(String level, String name);

	/**
	 * 根据对象的名称获取上下文环境中的一个对象
	 * 
	 * @param name
	 *            对象的名称
	 * @return 对象本身或者 null
	 */
	ObjectProxy fetch(String name);

	/**
	 * 清空缓存
	 */
	void clear();

	/**
	 * 销毁当前上下文对象，清空资源
	 */
	void depose();

}
