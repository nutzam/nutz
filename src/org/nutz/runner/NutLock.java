package org.nutz.runner;

/**
 * 锁对象,用来控制Runner的停止
 * 
 * @author zozoh
 * @author pw
 */
public class NutLock {

    private boolean stop;
    
    /**
     * 锁对象
     */
    public NutLock() {}

    /**
     * 是否已经停止
     * @return true,如果已经停止
     */
    public boolean isStop() {
        return stop;
    }

    /**
     * 设置停止位
     * @param stop 是否停止
     * @return 当前对象,用于链式赋值
     */
    public NutLock setStop(boolean stop) {
        this.stop = stop;
        return this;
    }

    /**
     * 设置为停止
     * @return 当前对象,用于链式赋值
     */
    public NutLock stop() {
        return setStop(true);
    }

    /**
     * 唤醒所有等待本对象的线程
     */
    public void wakeup() {
        synchronized (this) {
            this.notifyAll();
        }
    }
}
