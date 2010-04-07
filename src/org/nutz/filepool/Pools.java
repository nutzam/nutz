package org.nutz.filepool;

import java.io.File;

import org.nutz.lang.Files;

/**
 * 文件池的一些帮助函数
 * 
 * @author zozoh(zozohtnt@gmail.com)
 * @author wendal(wendal1985@gmail.com)
 */
public class Pools {

	public static File getFileById(File home, int id, String suffix) {
		return new File(getFilePathById(home, id, suffix));
	}

	public static String getFilePathById(File home, int id, String suffix) {
		StringBuilder sb = new StringBuilder(home.getAbsolutePath());
		sb.append(String.format("%016X", id).replaceAll("\\p{XDigit}{2}", "/$0"));
		if (null != suffix)
			sb.append(".").append(suffix);
		return sb.toString();
	}

	public static int getFileId(File home, File f) {
		if (f !=null && f.exists())
			return Integer.parseInt(Files.getMajorName(f), 16);
		return -1;
	}

}
