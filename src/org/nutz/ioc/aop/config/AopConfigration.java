package org.nutz.ioc.aop.config;

import java.util.List;

import org.nutz.ioc.Ioc;

/**
 * 配置Aop的通用接口
 * 
 * @author wendal(wendal1985@gmail.com)
 * 
 */
public interface AopConfigration {

    /**
     * 本对象在Ioc中的名字
     */
    String IOCNAME = "$aop";

    /**
     * 获取需要method与拦截器的对应关系,建议不要返回null
     * @param ioc 如果拦截器来自ioc容器,则需要提供这个参数
     * @param clazz 需要拦截的类
     * @return method与拦截器的对应关系
     */
    List<InterceptorPair> getInterceptorPairList(Ioc ioc, Class<?> clazz);

}
