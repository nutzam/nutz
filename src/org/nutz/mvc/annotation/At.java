package org.nutz.mvc.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 声明了一组URL。
 * <p>
 * 这个注解可以声明在模块上，也可以声明在每个模块的入口函数里。
 * <p>
 * <b> 如果声明在模块上：</b>
 * <ul>
 * <li>如果没有设值，那么默认的，就采用模块类名小写作为路径
 * <li>如果声明了多个值，那么一个模块就可能会有多个前缀
 * </ul>
 * 比如下面的例子表示， 整个 Abc 模块各个入口函数的地址以 以 /abc 开头:
 * 
 * <pre>
 * &#064;At
 * public class Abc {}
 * </pre>
 * 
 * 又比如这个例子表示，整个模块各个入口函数的地址以 /uuu 或 /ddd 开头
 * 
 * <pre>
 * &#064;At({&quot;/uuu&quot;, &quot;/ddd&quot;})
 * public class Abc {}
 * </pre>
 * 
 * <p>
 * <b>如果声明在入口函数上</b>
 * <ul>
 * <li>如果没有设值，那么默认的，就采用函数名小写作为路径
 * <li>如果声明了多个值，那么一个入口函数就可能会有多个地址，并且这个地址会和模块的地址做“笛卡尔积”<br>
 * <i>就是说，如果模块有两个地址，一个入口函数有两个地址，那么这个入口函数实际上有 2 x 2 = 4 个地址</i>
 * </ul>
 * 
 * 比如下面的例子，入口函数地址为 ：<b>/abc/hello</b>
 * 
 * <pre>
 * &#064;At
 * public class Abc {
 *     &#064;At
 *     public String hello() {
 *         return &quot;Hello&quot;;
 *     }
 * }
 * </pre>
 * 
 * 而这个例子，入口函数可以支持4个地址：
 * <ul>
 * <li><b>/uuu/say</b>
 * <li><b>/uuu/hello</b>
 * <li><b>/ddd/say</b>
 * <li><b>/ddd/hello</b>
 * </ul>
 * 
 * <pre>
 * &#064;At(&quot;/uuu&quot;, &quot;/ddd&quot;)
 * public class Abc{
 *     &#064;At(&quot;/say&quot;,&quot;/hello&quot;)
 *     public String hello() {
 *         return &quot;Hello&quot;;
 *     }
 * }
 * </pre>
 * 
 * @author zozoh(zozohtnt@gmail.com)
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
@Documented
public @interface At {

    /**
     * 如果为这个映射声明一个 key，哪么，可以通过 key 来访问这个 @At 的一个值
     * <p>
     * 默认的，你不需要关心这个属性
     */
    String key() default "";

    String[] value() default {};

}
