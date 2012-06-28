package org.nutz.mvc.upload.speed;

import java.io.File;

import org.junit.Ignore;
import org.nutz.lang.Files;
import org.nutz.lang.Lang;
import org.nutz.lang.Stopwatch;
import org.nutz.mock.servlet.MockHttpServletRequest;
import org.nutz.mvc.upload.*;
import org.nutz.trans.Atom;

import static org.nutz.mock.Mock.servlet.*;
import static java.lang.System.*;

@Ignore
public class UploadingSpeedTest {

    public static void main(String[] args) {
        if (0 == args.length) {
            System.err.println("Lack files directory!");
            System.exit(0);
        }

        File dir = Files.findFile(args[0]);
        if (null == dir) {
            System.err.println("Fail to found directory: " + args[0]);
            System.exit(0);
        }

        final Uploading up = UploadUnit.TYPE.born();
        final UploadingContext uc = UploadingContext.create("~/nutz/unit/uploadtmp");

        File[] files = dir.listFiles();
        final MockHttpServletRequest req = request().setInputStream(insmulti("UTF-8", files));
        req.setSession(session(context()));
        req.init();

        Object monLock = new Object();
        int monInterval = 2000;
        UploadMonitor mon = new UploadMonitor(monLock, req.getSession(), out, monInterval);
        Thread monThread = new Thread(mon, "UploadingMonitor");
        monThread.start();

        out.println("Begin...");
        Stopwatch sw = null;
        try {
            sw = Stopwatch.run(new Atom() {
                public void run() {
                    try {
                        up.parse(req, uc);
                    }
                    catch (UploadException e) {
                        throw Lang.wrapThrow(e);
                    }
                }
            });

        }
        catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            mon.stop();
            out.println("\n...Done!");
            if (null != sw)
                out.println(sw);
        }

    }

}
