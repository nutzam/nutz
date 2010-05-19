package org.nutz.aop;

import java.lang.reflect.Method;
import java.util.List;

import org.nutz.log.Log;
import org.nutz.log.Logs;

public class InterceptorChain {

	protected Method callingMethod;
	
	protected int methodIndex;
	
	protected Object args [];
	
	protected AopCallback callingObj;
	
	protected Object returnValue;
	
	protected List<MethodInterceptor> miList;
	
	private static Log LOG = Logs.getLog(InterceptorChain.class);
	
	private boolean invoked = false;
	
	private int currentMI = 0;
	
	public InterceptorChain(int methodIndex, Object obj , Method method , List<MethodInterceptor> miList ,Object [] args) {
		this.methodIndex = methodIndex;
		this.callingObj = (AopCallback) obj;
		this.callingMethod = method;
		this.args = args;
		this.miList = miList;
	}
	
	public InterceptorChain doChain() throws Throwable {
		if (currentMI == miList.size())
			invoke();
		else {
			currentMI++;
			miList.get(currentMI - 1).filter(this);
		}
		return this;
		
	}
	
	public void invoke() throws Throwable {
		if (invoked)
			LOG.warnf("!! Calling Method more than once! Method --> %s",callingMethod.toString());
		this.returnValue = callingObj._aop_invoke(methodIndex, args);
		invoked = true;
	}
	
	public Object getReturn(){
		return returnValue;
	}
	
	public Method getCallingMethod() {
		return callingMethod;
	}
	
	public Object[] getArgs() {
		return args;
	}

	public Object getReturnValue() {
		return returnValue;
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
	
	
}
