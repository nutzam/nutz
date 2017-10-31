package org.nutz.mvc;

import java.lang.reflect.Method;

/**
 * 入口方法判断器
 * 
 * @author 幸福的旁边(happyday517@163.com)
 */
public interface EntryMethodDeterminer {

    boolean isEntryMethod(Class<?> module,Method method);
}
