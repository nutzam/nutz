package org.nutz.mvc.upload.speed;

import java.io.File;

import org.nutz.filepool.FilePool;
import org.nutz.filepool.NutFilePool;
import org.nutz.lang.Files;
import org.nutz.lang.Lang;
import org.nutz.lang.Stopwatch;
import org.nutz.mock.servlet.MockHttpServletRequest;
import org.nutz.mvc.upload.SimpleUploading;
import org.nutz.mvc.upload.UploadException;
import org.nutz.mvc.upload.Uploading;
import org.nutz.trans.Atom;

import static org.nutz.mock.Mock.servlet.*;
import static java.lang.System.*;

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

		final Uploading up = new SimpleUploading(8192);
		final FilePool tmps = new NutFilePool("~/nutz/junit/uploadtmp");
		final String charset = "UTF-8";

		File[] files = dir.listFiles();
		final MockHttpServletRequest req = request().setInputStream(insmulti(files));
		req.setSession(session(context()));
		req.init();

		Object monLock = new Object();
		int monInterval = 2000;
		UploadMonitor mon = new UploadMonitor(monLock, req.getSession(), out, monInterval);
		Thread monThread = new Thread(mon, "UploadingMonitor");
		monThread.start();

		out.println("Begin...");
		Stopwatch sw = Stopwatch.run(new Atom() {
			public void run() {
				try {
					up.parse(req, charset, tmps);
				}
				catch (UploadException e) {
					throw Lang.wrapThrow(e);
				}
			}
		});
		mon.stop();
		out.println("\n...Done!");
		out.println(sw);
	}

}
