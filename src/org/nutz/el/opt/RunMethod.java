package org.nutz.el.opt;

import java.util.List;

/**
 * 方法执行接口.<br>
 * 供 MethodOpt 在执行方法的时候使用.<br>
 * 所有要进行方法执行的操作都需要实现这个接口,包括对象自身的方法,以及各自定义函数.<br>
 * <br>
 * 怎么实现自定义函数:<br>
 * <ul>
 * <li>实现本接口
 * <li>将自定义的配置信息以JSON的形式写入一个js文件中，并将文件放入'org/nutz/el/opt/custom/'包目录下.
 *  请参考'org/nutz/el/opt/custom/plugin.js'
 * </ul>
 * @author juqkai(juqkai@gmail.com)
 *
 */
public interface RunMethod {

    /**
     * 根据传入的参数执行方法
     * @param fetchParam 参数, 即EL表达式中, 函数括号内的内容.
     */
    Object run(List<Object> fetchParam);

    /**
     * 取得方法自身的符号
     */
    String fetchSelf();
}
