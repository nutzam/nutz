package org.nutz.aop;

/**
 * <b>不要实现这个接口</b><br/>
 * <b>不要实现这个接口</b><br/>
 * <b>不要实现这个接口</b><br/>
 * <b>不要实现这个接口</b><br/>
 * <b>不要实现这个接口</b><br/>
 * <b>不要实现这个接口</b><br/>
 * <b>不要实现这个接口</b><br/>
 * <b>不要实现这个接口</b><br/>
 * <b>这个接口仅供构建Aop类使用</b><br/>
 * <br/>
 * 这个接口将添加到被Aop改造过的类,如果你实现本接口,将导致不可预知的情况发生!!
 * 
 * @author wendal(wendal1985@gmail.com)
 * 
 */
public interface AopCallback {

    Object _aop_invoke(int methodIndex, Object[] args) throws Throwable ;

}
