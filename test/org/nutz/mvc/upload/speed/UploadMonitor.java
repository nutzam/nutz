package org.nutz.mvc.upload.speed;

import java.io.PrintStream;

import javax.servlet.http.HttpSession;

import org.nutz.lang.Lang;
import org.nutz.mvc.upload.UploadInfo;
import org.nutz.trans.Atom;

public class UploadMonitor implements Atom {

    private HttpSession session;
    private PrintStream out;
    private long interval;
    private Object lock;
    private boolean done;

    public UploadMonitor(Object lock, HttpSession session, PrintStream out, long interval) {
        this.lock = lock;
        this.session = session;
        this.out = out;
        this.interval = interval;
    }

    public UploadMonitor stop() {
        done = true;
        synchronized (lock) {
            lock.notifyAll();
        }
        return this;
    }

    public void run() {
        while (!done) {
            try {
                UploadInfo info = (UploadInfo) session.getAttribute(UploadInfo.SESSION_NAME);
                if (null == info) {
                    out.print('.');
                    synchronized (lock) {
                        lock.wait(interval);
                    }
                    continue;
                }
                out.printf(    "\n > %6s%% : %10d / %d",
                            (info.current / (info.sum / 100)),
                            info.current,
                            info.sum);
                synchronized (lock) {
                    lock.wait(interval);
                }
            }
            catch (InterruptedException e) {
                throw Lang.wrapThrow(e);
            }
        }
        out.printf("\n > %6s%%\n", 100);
    }
}
