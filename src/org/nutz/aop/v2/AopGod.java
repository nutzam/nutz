package org.nutz.aop.v2;

import java.lang.reflect.Method;
import java.util.ArrayList;

import org.nutz.aop.InterceptorChain;
import org.nutz.aop.MethodInterceptor;

public class AopGod {

	public int index;
	public Method method;
	public ArrayList<MethodInterceptor> interceptors;

	public void return_void(Object obj, Object[] args) throws Throwable {
		new InterceptorChain(index, obj, method, interceptors, args).doChain();
	}
	public int return_int(Object obj, Object[] args) throws Throwable {
		return ((Number)new InterceptorChain(index, obj, method, interceptors, args).doChain().getReturn()).intValue();
	}
	public short return_short(Object obj, Object[] args) throws Throwable {
		return ((Number)new InterceptorChain(index, obj, method, interceptors, args).doChain().getReturn()).shortValue();
	}
	public long return_long(Object obj, Object[] args) throws Throwable {
		return ((Number)new InterceptorChain(index, obj, method, interceptors, args).doChain().getReturn()).longValue();
	}
	public byte return_byte(Object obj, Object[] args) throws Throwable {
		return ((Number)new InterceptorChain(index, obj, method, interceptors, args).doChain().getReturn()).byteValue();
	}
	public char return_char(Object obj, Object[] args) throws Throwable {
		return ((Character)new InterceptorChain(index, obj, method, interceptors, args).doChain().getReturn());
	}
	public boolean return_boolean(Object obj, Object[] args) throws Throwable {
		return (Boolean)new InterceptorChain(index, obj, method, interceptors, args).doChain().getReturn();
	}
	public double return_double(Object obj, Object[] args) throws Throwable {
		return ((Number)new InterceptorChain(index, obj, method, interceptors, args).doChain().getReturn()).doubleValue();
	}
	public float return_float(Object obj, Object[] args) throws Throwable {
		return ((Number)new InterceptorChain(index, obj, method, interceptors, args).doChain().getReturn()).floatValue();
	}
	public Object return_Object(Object obj, Object[] args) throws Throwable {
		return new InterceptorChain(index, obj, method, interceptors, args).doChain().getReturn();
	}
}
