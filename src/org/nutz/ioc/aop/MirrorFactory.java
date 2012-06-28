package org.nutz.ioc.aop;

import org.nutz.lang.Mirror;

public interface MirrorFactory {

    /**
     * 根据一个类型生成 Mirror 对象
     */
    <T> Mirror<T> getMirror(Class<T> type, String name);

}
