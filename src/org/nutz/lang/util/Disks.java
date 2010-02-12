package org.nutz.lang.util;

import java.io.File;

import java.io.FilenameFilter;
import java.util.LinkedList;

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
		return getRelativePath(base.getAbsolutePath(), file.getAbsolutePath());
	}

	/**
	 * 将两个路径比较，得出相对路径
	 * 
	 * @param base
	 *            基础路径
	 * @param path
	 *            相对文件路径
	 * @return 相对于基础路径对象的相对路径
	 */
	public static String getRelativePath(String base, String path) {
		String[] bb = Strings.splitIgnoreBlank(getCanonicalPath(base), "[\\\\/]");
		String[] ff = Strings.splitIgnoreBlank(getCanonicalPath(path), "[\\\\/]");
		int len = Math.min(bb.length, ff.length);
		int pos = 0;
		for (; pos < len; pos++)
			if (!bb[pos].equals(ff[pos]))
				break;
		String re = Strings.dup("..", bb.length - pos);
		re += Lang.concat(pos, ff.length - pos, '/', ff);
		return re;
	}

	/**
	 * 整理路径。 将会合并路径中的 ".."
	 * 
	 * @param path
	 *            路径
	 * @return 整理后的路径
	 */
	public static String getCanonicalPath(String path) {
		String[] pa = Strings.splitIgnoreBlank(path, "[\\\\/]");
		LinkedList<String> paths = new LinkedList<String>();
		for (String s : pa) {
			if ("..".equals(s)) {
				if (paths.size() > 0)
					paths.removeLast();
				continue;
			} else {
				paths.add(s);
			}
		}
		StringBuilder sb = new StringBuilder();
		for (String s : paths) {
			sb.append("/").append(s);
		}
		return sb.deleteCharAt(0).toString();
	}
}
