package org.nutz.filepool;

import java.io.File;

public class Utils {

	public static File getFileById(File home, long id, String suffix) {
		return new File(getFilePathById(home, id, suffix));
	}

	public static String getFilePathById(File home, long id, String suffix) {
		StringBuilder sb = new StringBuilder(home.getAbsolutePath());
		sb.append(String.format("%016X", id).replaceAll("\\p{XDigit}{2}", "/$0"));
		if (null != suffix)
			sb.append(suffix);
		return sb.toString();
	}

	public static long getFileId(File home, File f) {
		String path = f.getAbsolutePath();
		String s = path.substring(home.getAbsolutePath().length(), path.lastIndexOf('.'));
		return Long.valueOf(s.replaceAll("[\\\\/]", ""), 16).longValue();
	}

}
