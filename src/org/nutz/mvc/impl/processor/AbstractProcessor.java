package org.nutz.mvc.impl.processor;

import org.nutz.mvc.ActionContext;
import org.nutz.mvc.ActionInfo;
import org.nutz.mvc.NutConfig;
import org.nutz.mvc.ObjectInfo;
import org.nutz.mvc.Processor;
import org.nutz.mvc.impl.Loadings;

/**
 * 抽象的Processor实现. 任何Processor实现都应该继承这个类,以获取正确的执行逻辑.
 * <p/>
 * @author zozoh(zozohtnt@gmail.com)
 * @author wendal(wendal1985@gmail.com)
 *
 */
public abstract class AbstractProcessor implements Processor {

    private Processor next;
    
    /**
     * 建议覆盖这个方法,以便从NutConfig/ActionInfo获取需要的信息
     */
    public void init(NutConfig config, ActionInfo ai) throws Throwable {
    }

    /**
     * 设置下一个Processor
     * <p/><b>一般情形下都不应该覆盖这个方法<b>
     * @param next 下一个Processor,一般不为null
     */
    public void setNext(Processor next) {
        this.next = next;
    }

    /**
     * 继续执行下一个Processor
     * <p/><b>一般情形下都不应该覆盖这个方法<b>
     * @param ac 执行方法的上下文
     * @throws Throwable
     */
    protected void doNext(ActionContext ac) throws Throwable {
        if (null != next)
            next.process(ac);
    }

    protected static <T> T evalObj(NutConfig config, ObjectInfo<T> info) {
        return null == info ? null : Loadings.evalObj(config, info.getType(), info.getArgs());
    }

    protected void renderView(ActionContext ac) throws Throwable {
        Processor p = next;
        while (p != null) {
            if (p instanceof ViewProcessor) {
                p.process(ac);
                return;
            }
            p = p.getNext();
        }
    }
    
    public Processor getNext() {
        return next;
    }
}
