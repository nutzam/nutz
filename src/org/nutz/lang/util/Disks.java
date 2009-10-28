package org.nutz.lang.util;

import java.io.File;
import java.io.FilenameFilter;

import org.nutz.lang.Lang;
import org.nutz.lang.Strings;

public abstract class Disks {

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

	public static String getRelativePath(File base, File file) {
		if (base.isFile())
			base = base.getParentFile();
		String[] bb = Strings.splitIgnoreBlank(base.getAbsolutePath(), "[\\\\/]");
		String[] ff = Strings.splitIgnoreBlank(file.getAbsolutePath(), "[\\\\/]");
		int pos = 0;
		for (; pos < Math.min(bb.length, ff.length); pos++)
			if (!bb[pos].equals(ff[pos]))
				break;
		String path = Strings.dup("../", bb.length - pos);
		path += Lang.concatBy(pos, ff.length - pos, '/', ff);
		return path;
	}
}
