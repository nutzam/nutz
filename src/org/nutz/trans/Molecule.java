package org.nutz.trans;

public abstract class Molecule<T> implements Atom {

    private T obj;

    public T getObj() {
        return obj;
    }

    public void setObj(T obj) {
        this.obj = obj;
    }

}
