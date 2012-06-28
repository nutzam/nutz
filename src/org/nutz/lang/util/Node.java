package org.nutz.lang.util;

import java.util.Iterator;
import java.util.List;

/**
 * 树节点。
 * 
 * @author zozoh(zozohtnt@gmail.com)
 * 
 * @param <T>
 */
public interface Node<T> {

    T get();

    Node<T> set(T obj);

    Node<T> parent(Node<T> node);

    Node<T> parent();

    Node<T> top();

    Node<T> prev();

    Node<T> prev(Node<T> node);

    Node<T> prev(int index);

    Node<T> next();

    Node<T> next(Node<T> node);

    Node<T> next(int index);

    Node<T> add(Node<?>... nodes);

    Node<T> addFirst(Node<T> node);

    Node<T> pop();

    Node<T> popFirst();

    Node<T> removeChild(int index);
    
    int getIndex();
    
    /**
     * @return true 成功移除，false 不能移除
     */
    boolean remove();

    Node<T> clearChildren();

    Node<T> insertBefore(int index, Node<T> node);

    boolean isRoot();

    boolean isLast();

    boolean isFirst();

    List<Node<T>> getAncestors();

    int depth();

    List<Node<T>> getNextSibling();

    List<Node<T>> getPrevSibling();

    int index();

    List<Node<T>> getChildren();

    Node<T> child(int index);

    Node<T> desc(int... indexes);

    int countChildren();

    boolean hasChild();

    Node<T> firstChild();

    Node<T> lastChild();

    Iterator<Node<T>> iterator();
}