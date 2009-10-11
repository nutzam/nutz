package org.nutz.mvc2;

/**
 * 这是一个扩展点，你可以通过实现这个接口，让注解 @Ok和 @Fail 支持更多的模板引擎。 这两个注解值的格式为：
 * 
 * <pre>
 * 视图类型:值
 * </pre>
 * 
 * 比如 jsp:abc.bbc.cbc
 * <p>
 * 为了支持更多的模板引擎，你可以自己实现一个 View，以及一个 ViewMaker。并且把你的 ViewMaker 配置在 web.xml 中 即可。
 * NutzServlet 支持一个参数叫 views ， 值为逗号分隔的 ViewMaker 实现类的全名。
 * <p>
 * <b>!!!请注意:</b>，你的实现类必须有一个 public 的默认构造函数，否则，框架将不知道如何实例化你的类。
 * 
 * @author zozoh
 * 
 */
public interface ViewMaker {

	View make(String type, String value);

}
