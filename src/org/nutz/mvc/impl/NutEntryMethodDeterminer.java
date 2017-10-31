package org.nutz.mvc.impl;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import org.nutz.lang.Mirror;
import org.nutz.mvc.EntryMethodDeterminer;
import org.nutz.mvc.annotation.At;
/**
 * 入口方法判断器默认实现，与之前版本行为一致</p>
 * 本实现默认继承父类所有入口方法，例如：
 * <pre>
 *   // 父类
 *   public abstract class Base {
 *       &#064;At public void sayhi() {}
 *       &#064;At public void saybye() {}
 *   }
 *   
 *   // 子类
 *   &#064;At("/my")
 *   public class MyModule extends Base {}
 * </pre>
 * 将添加映射
 * <ul>
 *  <li>/my/sayhi
 *  <li>/my/saybye
 * </ul>
 * @author 幸福的旁边(happyday517@163.com)
 */
public class NutEntryMethodDeterminer implements EntryMethodDeterminer {

    @Override
    public boolean isEntryMethod(Class<?> module, Method method) {
        if (!Modifier.isPublic(method.getModifiers()) || method.isBridge())
            return false;
        if (Mirror.getAnnotationDeep(method, At.class) == null)
            return false;
        return true;
    }

}
