package org.nutz.mvc.impl.processor;

import org.nutz.mvc.ActionContext;
import org.nutz.mvc.impl.Processor;

public abstract class AbstractProcessor implements Processor {

	protected Processor next;

	public void setNext(Processor next) {
		this.next = next;
	}

	public Processor getNext() {
		return next;
	}

	public Processor process(ActionContext ac) throws Throwable {
		doProcess(ac);
		return getNext();
	}
	
	/**
	 * 继续执行其他处理器
	 * <p/><b>如果调用本方法,process方法务必返回null,否则动作链将再次被执行</b>
	 * <p/><b>如果调用本方法,process方法务必返回null,否则动作链将再次被执行</b>
	 * <p/><b>不应该在doProcess方法中调用本方法</b>
	 * <p/>调用示例:<p/>
	 * <code>
	 * logger.info("已经授权,继续执行");<p/>
	 * doChain(ac, getNext());<p/>
	 * logger.info("处理完成,将请求记录到数据库");
	 * </code>
	 */
	protected static final void doChain(ActionContext ac, Processor p) throws Throwable {
		if (null != p)//还有其他处理器?那继续吧
			while (null != (p = p.process(ac))) {}
	}

	/**
	 * 一般都应该覆盖这个方法,默认实现不做任何事情
	 */
	public void doProcess(ActionContext ac) throws Throwable {
	}

}
