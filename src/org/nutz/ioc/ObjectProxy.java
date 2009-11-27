package org.nutz.ioc;

/**
 * 每次获取对象时会触发 fetch 事件，销毁时触发 depose 事件。
 * <p>
 * 这个对象需要小心被创建和使用。为了防止循环注入的问题，通常，ObjectMaker 需要快速<br>
 * 创建一个 ObjectProxy， 存入上下文。 然后慢慢的设置它的 weaver 和 fetch。
 * <p>
 * 在出现异常的时候，一定要将该对象从上下文中移除掉。
 * 
 * @author zozoh(zozohtnt@gmail.com)
 */
public class ObjectProxy {

	/**
	 *对象编织方式
	 */
	private ObjectWeaver weaver;

	/**
	 *获取时触发器
	 */
	private IocEventTrigger<Object> fetch;

	public ObjectProxy setWeaver(ObjectWeaver weaver) {
		this.weaver = weaver;
		return this;
	}

	public ObjectProxy setFetch(IocEventTrigger<Object> fetch) {
		this.fetch = fetch;
		return this;
	}

	@SuppressWarnings("unchecked")
	public <T> T get(Class<T> classOfT, IocMaking ing) {
		Object obj = weaver.weave(ing);
		if (null != fetch)
			fetch.trigger(obj);
		return (T) obj;
	}

	public void depose() {
		weaver.depose();
	}

}
