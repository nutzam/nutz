package org.nutz.lang.random;

public class RecurArrayRandom<T> implements Random<T> {

    private T[] array;
    private java.util.Random r = new java.util.Random();

    public RecurArrayRandom(T[] array) {
        this.array = array;
    }

    public T next() {
        if(array == null || array.length ==0) return null;
        return array[r.nextInt(array.length)];
    }

}
