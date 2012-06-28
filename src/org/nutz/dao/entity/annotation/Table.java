package org.nutz.dao.entity.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 声明的一个 POJO 所对应的数据表名。
 * 
 * <h4>动态数据表名的支持</h4><br>
 * 
 * 注解支持字符串模板的写法，在你希望放置动态表名变量的位置插入 ${变量名}，比如：
 * 
 * <pre>
 * &#064;Table(&quot;t_tab_${cid}&quot;)
 * public class MyPojo{
 *     ...
 * </pre>
 * 
 * 那么 ${cid} 会在运行时被 Nutz.Dao 替换。
 * <p>
 * 如何为动态表名设置参数，请参看 <b>org.nutz.dao.TableName</b> 的文档说明
 * <p>
 * <h4>动态表名的赋值规则</h4><br>
 * <ul type="disc">
 * <li>当传入参数为数字或字符串
 * <ul type="circle">
 * <li>所有的动态表名变量将被其替换</li>
 * </ul>
 * </li>
 * <li>当传入参数为 Map
 * <ul type="circle">
 * <li>按照动态表明变量的名称在 Map 中查找值，并进行替换</li>
 * <li>大小写敏感</li>
 * <li>未找到的变量将被空串替换</li>
 * </ul>
 * </li>
 * <li>当传入参数为 任意Java对象(POJO)
 * <ul type="circle">
 * <li>按照动态表明变量名称在对象中查找对应字段的值，并进行替换</li>
 * <li>大小写敏感</li>
 * <li>未找到的变量将被空串替换</li>
 * </ul>
 * </li>
 * <li>当传入参数为null
 * <ul type="circle">
 * <li>所有变量将被空串替换</li>
 * </ul>
 * <div class="hr"></div></li>
 * </ul>
 * </ol>
 * 
 * @author zozoh
 * 
 * @see org.nutz.dao.TableName
 * 
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Documented
public @interface Table {

    String value();

}
