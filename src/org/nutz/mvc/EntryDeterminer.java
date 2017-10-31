package org.nutz.mvc;

import java.lang.reflect.Method;

/**
 * 入口方法决断器
 * 
 * @author 幸福的旁边(happyday517@163.com)
 */
public interface EntryDeterminer {

    /**
     * 决断给定方法是否是入口方法
     */
    boolean isEntry(Class<?> module,Method method);
}
