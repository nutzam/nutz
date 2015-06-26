package org.nutz.trans;

/**
 * 用于传输匿名内部类返回值
 * @author wendal(wendal1985@gmail.com)
 *
 * @param <T> 返回值的类型
 */
public abstract class Molecule<T> implements Atom {
    
    /**
     * 一般以匿名内部类的方式构建
     */
    public Molecule() {}

    private T obj;

    /**
     * Trans.exec执行完毕后获取返回值
     * @return setObj方法所设置的方法
     */
    public T getObj() {
        return obj;
    }

    /**
     * 通常在匿名内部类中调用,这样getObj就能拿到值
     * @param obj 需要传出去的返回值
     */
    public void setObj(T obj) {
        this.obj = obj;
    }

}
