package org.nutz.lang.random;

import java.util.List;

/**
 * 根据一个列表随机产生对象，每个对象只会被取出一次。 当数组耗尽，则一直返回 null
 * 
 * @author Conanca
 */
public class ListRandom<T> implements Random<T> {

    private List<T> list;
    private Integer len;
    private java.util.Random r = new java.util.Random();
    private Object lock = new Object();

    public ListRandom(List<T> list) {
        this.list = list;
        len = list.size();
    }

    public T next() {
        synchronized (lock) {
            if (len <= 0)
                return null;
            if (len == 1)
                return list.get(--len);
            int index = r.nextInt(len);
            if (index == len - 1)
                return list.get(--len);
            T c = list.get(index);
            list.set(index, list.get(--len));
            return c;
        }
    }
}
