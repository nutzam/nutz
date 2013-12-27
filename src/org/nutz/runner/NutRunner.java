package org.nutz.runner;

import java.util.Date;

import org.nutz.lang.Times;
import org.nutz.log.Log;
import org.nutz.log.Logs;

/**
 * 所有后台运行的业务逻辑均需集成本类
 * 
 * @author zozoh(zozohtnt@gmail.com)
 * @author pw
 */
public abstract class NutRunner implements Runnable {

    protected Log log;

    protected Thread myThread;

    /**
     * 本运行器名称
     */
    protected String rnm;

    /**
     * 线程锁
     */
    protected NutLock lock;

    /**
     * 累积启动次数
     */
    protected int count;

    /**
     * 本次睡眠时间
     */
    protected long interval;

    /**
     * 启动于
     */
    protected Date upAt;

    /**
     * 睡眠于，如果本值不为 null，表示本线程正在睡眠，否则为运行中
     */
    protected Date downAt;

    public NutRunner(String rname) {
        this.rnm = rname;
        this.count = 0;
        this.lock = new NutLock();
    }

    public void run() {
        // 判断下log是否已经初始化
        if (log == null) {
            log = Logs.get().setTag(rnm);
        }
        myThread = Thread.currentThread();
        // 线程开始运行，那么首先注册
        reg(this);
        // 干点什么吧, 可以覆盖掉
        doIt();
        // 线程结束后，取消注册
        unreg(this);
    }

    /**
     * 子类实现的业务逻辑
     * 
     * @return 本次运行后还需要等待多少个毫秒
     */
    public abstract long exec() throws Exception;

    /**
     * 注册本Runner
     * 
     * @param me
     *            对象本身
     */
    public abstract void reg(NutRunner me);

    /**
     * 注销本Runner
     * 
     * @param me
     *            对象本身
     */
    public abstract void unreg(NutRunner me);

    /**
     * 做一些需要定期执行的操作
     */
    public void doIt() {
        while (!lock.isStop()) {
            synchronized (lock) {
                try {
                    // 修改一下本线程的时间
                    upAt = Times.now();
                    downAt = null;
                    log.debugf("%s [%d] : up", rnm, ++count);

                    // 执行业务
                    interval = exec();

                    if (interval < 1)
                        interval = 1; // 不能间隔0或者负数,会死线程的

                    // 等待一个周期
                    downAt = Times.now();
                    log.debugf("%s [%d] : wait %ds(%dms)", rnm, count, interval / 1000, interval);
                    lock.wait(interval);
                }
                catch (InterruptedException e) {
                    log.warn(String.format("%s has been interrupted", rnm), e);
                    break;
                }
                catch (Throwable e) {
                    log.warn(String.format("%s has some error", rnm), e);
                    try {
                        lock.wait(30 * 1000);
                    }
                    catch (Throwable e1) {
                        log.warn(String.format("%s has some error again", rnm), e);
                        break;
                    }
                }
            }
        }
    }

    public String toString() {
        return String.format("[%s:%d] %s/%s - %d",
                             rnm,
                             count,
                             upAt   == null ? "NONE" : Times.sDT(upAt),
                             downAt == null ? "NONE" : Times.sDT(downAt),
                             interval);
    }

    public boolean isWaiting() {
        return null != downAt;
    }

    public boolean isRunning() {
        return null == downAt;
    }

    public long getInterval() {
        return interval;
    }

    public Date getUpAt() {
        return upAt;
    }

    public Date getDownAt() {
        return downAt;
    }

    public String getName() {
        return rnm;
    }

    public int getCount() {
        return count;
    }

    public NutLock getLock() {
        return lock;
    }

    public boolean isAlive() {
        if (myThread != null) {
            return myThread.isAlive();
        }
        return false;
    }

    @SuppressWarnings("deprecation")
    public void stop(Throwable err) {
        myThread.stop(err);
    }

}
