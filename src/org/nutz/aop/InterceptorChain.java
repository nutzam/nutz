package org.nutz.aop;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;

import org.nutz.log.Log;
import org.nutz.log.Logs;

/**
 * 拦截器链,持有被调用方法的信息
 * 
 * @author wendal(wendal1985@gmail.com)
 * 
 */
public class InterceptorChain {

    protected Method callingMethod;

    protected int methodIndex;

    protected Object args[];

    protected AopCallback callingObj;

    protected Object returnValue;

    protected List<MethodInterceptor> miList;

    private static Log LOG = Logs.get();

    private boolean invoked = false;

    private int currentMI = 0;

    public InterceptorChain(int methodIndex,
                            Object obj,
                            Method method,
                            List<MethodInterceptor> miList,
                            Object[] args) {
        this.methodIndex = methodIndex;
        this.callingObj = (AopCallback) obj;
        this.callingMethod = method;
        this.args = args;
        this.miList = miList;
    }

    /**
     * 继续执行下一个拦截器,如果已经没有剩下的拦截器,则执行原方法
     * 
     * @return 拦截器链本身
     * @throws Throwable
     *             下层拦截器或原方法抛出的一切异常
     */
    public InterceptorChain doChain() throws Throwable {
        if (currentMI == miList.size())
            invoke();
        else {
            currentMI++;
            miList.get(currentMI - 1).filter(this);
        }
        return this;

    }

    /**
     * 执行原方法,一般情况下不应该直接被调用
     * 
     * @throws Throwable
     *             原方法抛出的一切异常
     */
    public void invoke() throws Throwable {
        if (invoked)
            LOG.warnf("!! Calling Method more than once! Method --> %s", callingMethod.toString());
        else
            invoked = true;
        this.returnValue = callingObj._aop_invoke(methodIndex, args);
    }

    /**
     * 获取返回值
     * 
     * @return 返回值
     */
    public Object getReturn() {
        return returnValue;
    }

    /**
     * 正在被调用的Method
     */
    public Method getCallingMethod() {
        return callingMethod;
    }

    /**
     * 方法调用的参数数组,如果你要改变参数,那么必须保证参数类型与方法参数兼容.
     */
    public Object[] getArgs() {
        return args;
    }

    public void setReturnValue(Object returnValue) {
        this.returnValue = returnValue;
    }

    public AopCallback getCallingObj() {
        return callingObj;
    }

    public boolean isInvoked() {
        return invoked;
    }

    /**
     * 获取当前的方法拦截器列表,注意,这个列表是不可修改的.如果需要修改,那么请通过{@link #setMethodInterceptors(List)}
     */
    public List<MethodInterceptor> getMethodInterceptors() {
        return Collections.unmodifiableList(miList);
    }
    
    /**
     * 设置当前调用的方法拦截器列表,注意,这个set只对当前方法调用有效.
     */
    public void setMethodInterceptors(List<MethodInterceptor> miList) {
        this.miList = miList;
    }
}
