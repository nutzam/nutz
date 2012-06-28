package org.nutz.el.parse;

/**
 * 字符队列, 主要是为了解决reader 中使用的 cursor 临时变量的问题.
 * @author juqkai(juqkai@gmail.com)
 *
 */
public interface CharQueue {
    /**
     * 不删除字符的情况下读取第一个字符
     */
    char peek();
    /**
     * 不删除字符的情况下读取第ofset个字符,
     * @param ofset 偏移量
     */
    char peek(int ofset);
    /**
     * 读取字符,并删除字符
     */
    char poll();
    /**
     * 是否为空
     */
    boolean isEmpty();
}
