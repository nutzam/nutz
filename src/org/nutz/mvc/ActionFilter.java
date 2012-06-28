package org.nutz.mvc;


/**
 * 入口函数的过滤器，你的过滤器实现只需要实现一个函数 match。 这个函数如果你返回的是 null，表示你的过滤器认为，可以继续。 如果你的函数返回一个
 * View 对象，就表示你的过滤器认为这个请求有问题，不能继续进行下一步操作。 直接用返回的 View 渲染 response 即可。
 * <p>
 * 你可以通过 '@Filters' 以及 '@By' 为任何一个入口函数，或者模块声明你的过滤器。
 * <p>
 * 你的过滤器的构造函数的参数，要和你的 '@By' 的参数相匹配
 * 
 * @author zozoh(zozohtnt@gmail.com)
 */
public interface ActionFilter {

    View match(ActionContext actionContext);
}
