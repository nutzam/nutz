package org.nutz.trans;

/**
 * Atom接口的另外一种变种实现
 * @author wendal(wendal1985@gmail.com)
 *
 * @param <T> 返回值的类型
 */
public abstract class Proton<T> implements Atom {

    private T obj;
    
    /**
     * 通常在匿名内部类中初始化
     */
    public Proton() {}

    /**
     * exec方法的返回值
     * @return 方法返回值
     */
    public T get() {
        return obj;
    }

    /**
     * 用户代码的开始
     */
    public void run() {
        obj = exec();
    }

    /**
     * 需要子类实现的逻辑
     * @return 方法返回值
     */
    protected abstract T exec();

}
