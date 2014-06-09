package org.nutz.runner;

/**
 * 锁对象,用来控制Runner的停止
 * 
 * @author zozoh
 * @author pw
 */
public class NutLock {

    private boolean stop;

    public boolean isStop() {
        return stop;
    }

    public NutLock setStop(boolean stop) {
        this.stop = stop;
        return this;
    }

    public NutLock stop() {
        return setStop(true);
    }

    public void wakeup() {
        synchronized (this) {
            this.notifyAll();
        }
    }
}
