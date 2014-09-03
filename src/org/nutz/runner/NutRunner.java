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
        if (log == null) {
            log = Logs.get().setTag(rnm);
        }
        myThread = Thread.currentThread();

        beforeStart(this);
        doIt();
        afterStop(this);
    }

    /**
     * 具体的业务实现,返回一个sleep数
     * 
     * @return 本次运行后还需要等待多少个毫秒
     */
    public abstract long exec() throws Exception;

    @Deprecated
    public void reg(NutRunner me) {}

    @Deprecated
    public void unreg(NutRunner me) {};

    /**
     * 开始之前,一般做一些准备工作,比如资源初始化等
     * 
     * @param me
     *            runner本身
     */
    public void beforeStart(NutRunner me) {
        reg(me);
    };

    /**
     * 停止之后,一般是做一些资源回收
     * 
     * @param me
     *            runner本身
     */
    public void afterStop(NutRunner me) {
        unreg(me);
    }

    /**
     * 做一些需要定期执行的操作
     */
    protected void doIt() {
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
                             upAt == null ? "NONE" : Times.sDT(upAt),
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
