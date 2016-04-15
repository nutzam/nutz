package org.nutz.lang;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import org.nutz.lang.util.CronSequenceGenerator;

import org.nutz.log.Log;
import org.nutz.log.Logs;

/**
 * 定时任务服务的友好封装
 * @author QinerG(qinerg@gmail.com)
 */
public abstract class Tasks {

    private static Log logger = Logs.get();

    private static ScheduledThreadPoolExecutor taskScheduler = new ScheduledThreadPoolExecutor(getBestPoolSize());
    private static List<Timer> timerList = new ArrayList<Timer>();

    /**
     * 立即启动，并以固定的频率来运行任务。后续任务的启动时间不受前次任务延时影响。
     * @param task 具体待执行的任务
     * @param period 每次执行任务的间隔时间(单位秒)
     */
    public static ScheduledFuture<?> scheduleAtFixedRate(Runnable task, long periodSeconds) {
        return scheduleAtFixedRate(task, 0, periodSeconds, TimeUnit.SECONDS);
    }

    /**
     * 在指定的延时之后开始以固定的频率来运行任务。后续任务的启动时间不受前次任务延时影响。
     * @param task 具体待执行的任务
     * @param initialDelay 首次执行任务的延时时间
     * @param periodSeconds 每次执行任务的间隔时间(单位秒)
     * @param unit 时间单位
     */
    public static ScheduledFuture<?> scheduleAtFixedRate(Runnable task, long initialDelay, long period, TimeUnit unit) {
        return taskScheduler.scheduleAtFixedRate(task, initialDelay, period, unit);
    }

    /**
     * 在指定的时间点开始以固定的频率运行任务。后续任务的启动时间不受前次任务延时影响。
     * @param task 具体待执行的任务
     * @param startTime 首次运行的时间点,支持 "yyyy-MM-dd HH:mm:ss" 格式
     * @param period 每次执行任务的间隔时间
     * @param unit 时间单位
     */
    public static void scheduleAtFixedRate(Runnable task, String startTime, long period, TimeUnit unit) throws ParseException {
        Date dt = Times.D(startTime);
        scheduleAtFixedRate(task, dt, period, unit);
    }

    /**
     * 在指定的时间点开始以固定的频率运行任务。后续任务的启动时间不受前次任务延时影响。
     * @param task 具体待执行的任务
     * @param startTime 首次运行的时间点
     * @param period 每次执行任务的间隔时间
     * @param unit 时间单位
     */
    public static void scheduleAtFixedRate(final Runnable task, Date startTime, final long period, final TimeUnit unit) {
        final Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                taskScheduler.scheduleAtFixedRate(task, 0, period, unit);
                timer.cancel();
                timerList.remove(timer);
            }
        }, startTime);
        timerList.add(timer);
    }

    /**
     * 立即启动，两次任务间保持固定的时间间隔
     * @param task 具体待执行的任务
     * @param period 两次任务的间隔时间(单位秒)
     */
    public static ScheduledFuture<?> scheduleWithFixedDelay(Runnable task, long periodSeconds) {
        return scheduleWithFixedDelay(task, 0, periodSeconds, TimeUnit.SECONDS);
    }

    /**
     * 在指定的延时之后启动，两次任务间保持固定的时间间隔
     * @param task 具体待执行的任务
     * @param initialDelay 首次执行任务的延时时间
     * @param period 两次任务的间隔时间(单位秒)
     * @param unit 时间单位
     */
    public static ScheduledFuture<?> scheduleWithFixedDelay(Runnable task, long initialDelay, long period, TimeUnit unit) {
        return taskScheduler.scheduleWithFixedDelay(task, initialDelay, period, unit);
    }

    /**
     * 在指定的时间点启动，两次任务间保持固定的时间间隔
     * @param task 具体待执行的任务
     * @param startTime 首次运行的时间点,支持 "yyyy-MM-dd HH:mm:ss" 格式
     * @param period 两次任务的间隔时间
     * @param unit 时间单位
     */
    public static void scheduleWithFixedDelay(Runnable task, String startTime, long period, TimeUnit unit) throws ParseException {
        Date dt = Times.D(startTime);
        scheduleWithFixedDelay(task, dt, period, unit);
    }

    /**
     * 在指定的时间点启动，两次任务间保持固定的时间间隔
     * @param task 具体待执行的任务
     * @param startTime 首次运行的时间点
     * @param period 两次任务的间隔时间
     * @param unit 时间单位
     */
    public static void scheduleWithFixedDelay(final Runnable task, Date startTime, final long period, final TimeUnit unit) {
        final Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                taskScheduler.scheduleWithFixedDelay(task, 0, period, unit);
                timer.cancel();
                timerList.remove(timer);
            }
        }, startTime);
        timerList.add(timer);
    }
    /**
     * 在指定的时间点启动任务只运行一次
     * @param task 具体待执行的任务
     * @param startTime 运行的时间点
     */
    public static void scheduleAtFixedTime(final Runnable task, Date startTime) {
        final Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                taskScheduler.execute(task);
                timer.cancel();
                timerList.remove(timer);
            }
        }, startTime);
        timerList.add(timer);
    }
    /**
     * 在符合条件的时间点启动任务
     * @param task 具体待执行的任务
     * @param startTime 运行的时间点
     */
    public static void scheduleAtFixedTime(final Runnable task, String expression) {
        TimeSchedule timeSchedule = new TimeSchedule(task,expression);
        timeSchedule.start();
        
    }

    /**
     * 调整线程池大小
     * @param threadPoolSize
     */
    public static void resizeThreadPool(int threadPoolSize) {
        taskScheduler.setCorePoolSize(threadPoolSize);
    }

    /**
     * 返回定时任务线程池，可做更高级的应用
     * @return
     */
    public static ScheduledThreadPoolExecutor getTaskScheduler() {
        return taskScheduler;
    }

    /**
     * 关闭定时任务服务
     * <p>系统关闭时可调用此方法终止正在执行的定时任务，一旦关闭后不允许再向线程池中添加任务，否则会报RejectedExecutionException异常</p>
     */
    public static void depose() {
    	int timerNum = timerList.size();
    	//清除Timer
    	synchronized (timerList) {
    		for (Timer t: timerList)
    			t.cancel();
    		timerList.clear();
    	}
    	
        List<Runnable> awaitingExecution = taskScheduler.shutdownNow();
        logger.infof("Tasks stopping. Tasks awaiting execution: %d", timerNum + awaitingExecution.size());
    }

    /**
     * 重启动定时任务服务
     */
    public static void reset() {
        depose();
        taskScheduler = new ScheduledThreadPoolExecutor(getBestPoolSize());
    }

    /**
     * 根据 Java 虚拟机可用处理器数目返回最佳的线程数。<br>
     * 最佳的线程数 = CPU可用核心数 / (1 - 阻塞系数)，其中阻塞系数这里设为0.9
     */
    private static int getBestPoolSize() {
        try {
            // JVM可用处理器的个数
            final int cores = Runtime.getRuntime().availableProcessors();
            // 最佳的线程数 = CPU可用核心数 / (1 - 阻塞系数)
            // TODO 阻塞系数是不是需要有个setter方法能让使用者自由设置呢？
            return (int)(cores / (1 - 0.9));
        }
        catch (Throwable e) {
            // 异常发生时姑且返回10个任务线程池
            return 10;
        }
    }
    public static void main(String[] args) {
        Tasks.scheduleAtFixedTime(new Runnable(){
            @Override
            public void run() {
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss E");  
                Date date = new Date();
                System.out.println(format.format(date));  
                System.out.println("this task test");
                System.out.println("===============");
            }
        }, "0/5 * * * * *");
    }

}
class TimeSchedule implements Runnable{
    private final Runnable task;
    private final CronSequenceGenerator cron;

    public TimeSchedule(Runnable task, String expression) {
        this.task = task;
        this.cron =  new CronSequenceGenerator(expression);
    }

    public void start(){
        Date startTime = cron.next(new Date());
        Tasks.scheduleAtFixedTime(this,startTime);
    }

    @Override
    public void run() {
        task.run();
        start();
    }
}
