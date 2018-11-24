package org.nutz.lang.random;

/**
 * 根据一个数组随机产生对象，每个对象只会被取出一次。 当数组耗尽，则一直返回 null
 * 
 * @author zozoh(zozohtnt@gmail.com)
 */
public class ArrayRandom<T> implements Random<T> {

    private T[] array;
    private Integer len;
    private java.util.Random r = new java.util.Random();
    private Object lock = new Object();

    public ArrayRandom(T[] array) {
        this.array = array;
        len = array.length;
    }

    public T next() {
        synchronized (lock) {
            if (len <= 0)
                return null;
            if (len == 1)
                return array[--len];
            int index = r.nextInt(len);
            if (index == len - 1)
                return array[--len];
            T c = array[index];
            array[index] = array[--len];
            return c;
        }
    }

}
