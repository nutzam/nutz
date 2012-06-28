package org.nutz.mvc;

import java.lang.reflect.Method;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 这是一个处理 HTTP 请求的扩展点。通过它，你可以用任何你想要的方式来为你的入口函数准备参数。 默认的，框架为你提供了三个实现：
 * <ul>
 * <li>UploadAdaptor // 处理多文件上传
 * <li>JsonAdaptor // 处理 Json 字节流
 * <li>PairAdaptor // 按传统的名值对方式处理
 * </ul>
 * 你可以通过注解 '@AdaptBy' 来声明你的入口函数具体将采用哪个适配器，默认的 框架将采用 PairHttpAdaptor
 * 来适配参数。当然，你也可以声明你自己的适配方法
 * <p>
 * 你还需要知道的是：你每一个入口函数，框架都会为你建立一个新的适配器的实例。
 * <p>
 * <b>注意：</b>
 * <ul>
 * <li>如果你要写自己的实现类，这个类必须有一个public 的默认构造函数，如果你的构造函数需要参数，则必须是 String 类型的。参数的值由 注解
 * '@AdaptBy.args' 来填充，它的默认值为 长度为0的空字符串数组。
 * <li>适配器对于一个 URL只会有一份实例。
 * <li>容器假定你的适配器不会占有什么不可释放的资源，必要的时候，它会允许垃圾回收器回收你的适配器实例
 * </ul>
 * 
 * @author zozoh(zozohtnt@gmail.com)
 * 
 * @see org.nutz.mvc.annotation.AdaptBy
 */
public interface HttpAdaptor {

    /**
     * 这个函数将在你的适配器生命周期内，这个函数将只被调用一次。它用来告诉你的适配器，你需要适配什么方法。
     * 
     * @param method
     *            你需要适配的方法
     */
    void init(Method method);

    /**
     * 你的适配器需要根据传入的 request 和 response 生成函数的调用参数
     * 
     * @param sc
     *            Servlet 上下文对象
     * @param req
     *            请求对象
     * @param resp
     *            响应对象
     * @param pathArgs
     *            字符串数组，路径参数。详情请参看 <a
     *            href="http://code.google.com/p/nutz/wiki/mvc_http_adaptor#路径参数"
     *            >路径参数</a>
     * 
     * @return 调用参数数组
     * 
     */
    Object[] adapt(    ServletContext sc,
                    HttpServletRequest req,
                    HttpServletResponse resp,
                    String[] pathArgs);

}
