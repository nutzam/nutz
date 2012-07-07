package org.nutz.mvc.impl;

import java.lang.reflect.Method;

import org.nutz.mvc.adaptor.AbstractAdaptor;
import org.nutz.mvc.adaptor.ParamInjector;

/**
 * 当这个类作为参数声明在入口方法时,当适配器产生错误时,将注入到你的方法中</p>
 * 这是很普通的类,但,请发挥你的创造力,这可是一个扩展点啊! 你可以继承这个类,
 * @author wendal
 *
 */
public class AdaptorErrorContext {

    /**
     * 构建一个适配器错误上下文,由AbstractAdaptor创建</p>
     * 子类必须有这个构造方法!!
     * @param size 必须等于入口方法的参数数量
     */
    public AdaptorErrorContext(int size) {
        errors = new Throwable[size];
    }
    
    /**
     * 具体参数导致的异常信息
     */
    protected Throwable[] errors;
    
    /**
     * 获取具体参数的异常信息,与参数的顺序一致, 其中会包含null(尤其是最后一个参数,因为就是本类)
     */
    public Throwable[] getErrors() {
        return errors;
    }
    
    /**
     * 设置当前参数的错误信息,是子类可以无限扩展的地方
     * @param index 参数的索引号
     * @param err   参数注入器抛出的异常,建议先用Lang.unwarp(err)解开,获取真正的异常
     * @param method 入口方法
     * @param value  期待转换的值
     * @param inj    参数注入器
     */
    public void setError(int index, Throwable err, Method method, Object value, ParamInjector inj) {
        errors[index] = err;
    }
    
    /**
     * 适配器本身导致的异常,例如JsonAdaptor读取到错误的Json字符串, UploadAdaptor读取到错误的上传信息
     */
    protected Throwable adaptorErr;
    
    public void setAdaptorError(Throwable err, AbstractAdaptor adaptor) {
        this.adaptorErr = err;
    }
    
    /**
     * 适配器本身导致的异常,例如JsonAdaptor读取到错误的Json字符串, UploadAdaptor读取到错误的上传信息
     */
    public Throwable getAdaptorErr() {
        return adaptorErr;
    }
}
