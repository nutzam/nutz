package org.nutz.lang.util;

import java.io.File;
import java.io.FilenameFilter;

import org.nutz.lang.Lang;
import org.nutz.lang.Strings;

/**
 * 磁盘操作的帮助函数集合
 * 
 * @author zozoh(zozohtnt@gmail.com)
 */
public abstract class Disks {

	/**
	 * 一个 Vistor 模式的目录深层遍历
	 * 
	 * @param f
	 *            要遍历的目录或者文件，如果是目录，深层遍历，否则，只访问一次文件
	 * @param fv
	 *            对文件要进行的操作
	 * @param filter
	 *            遍历目录时，哪些文件应该被忽略
	 * @return 遍历的文件个数
	 */
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

	/**
	 * 将两个文件对象比较，得出相对路径
	 * 
	 * @param base
	 *            基础文件对象
	 * @param file
	 *            相对文件对象
	 * @return 相对于基础文件对象的相对路径
	 */
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
