package org.nutz.trans;

public abstract class Proton<T> implements Atom {

    private T obj;

    public T get() {
        return obj;
    }

    public void run() {
        obj = exec();
    }

    protected abstract T exec();

}
