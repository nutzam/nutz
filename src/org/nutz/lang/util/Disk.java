package org.nutz.lang.util;

import java.io.File;
import java.io.FilenameFilter;

public class Disk {

	public static int visitFile(File f, FileVisitor fv, FilenameFilter filter) {
		int re = 0;
		if (f.isFile()) {
			fv.visit(f);
			re++;
		} else if (f.isDirectory()) {
			File[] fs = null == filter ? f.listFiles() : f.listFiles(filter);
			for (File ff : fs)
				re += visitFile(ff, fv, filter);
		}
		return re;
	}

}
