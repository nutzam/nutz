package org.nutz.filepool;

import java.io.File;

/**
 * 文件池的一些帮助函数
 * 
 * @author zozoh(zozohtnt@gmail.com)
 */
public class Pools {

	public static File getFileById(File home, int id, String suffix) {
		return new File(getFilePathById(home, id, suffix));
	}

	public static String getFilePathById(File home, int id, String suffix) {
		StringBuilder sb = new StringBuilder(home.getAbsolutePath());
		sb.append(String.format("%016X", id).replaceAll("\\p{XDigit}{2}", "/$0"));
		if (null != suffix)
			sb.append(suffix);
		return sb.toString();
	}

	public static int getFileId(File home, File f) {
		String path = f.getAbsolutePath();
		int pos = path.lastIndexOf('.');
		String s = pos > 0	? path.substring(home.getAbsolutePath().length(), pos)
							: path.substring(home.getAbsolutePath().length());
		return Integer.parseInt(s.replaceAll("[\\\\/]", ""), 16);
	}

}
