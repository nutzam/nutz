package com.zzh.lang;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;


public class Files {
	public static String addSuffixInFileName(String name, String suffix) {
		if (null == name)
			return null;
		if (null == suffix || suffix.length() == 0)
			return name;
		int pos = name.lastIndexOf('.');
		if (-1 == pos)
			return name + suffix;
		return name.substring(0, pos) + suffix + name.substring(pos);
	}

	public static String getExtension(File f) {
		if (null == f)
			return null;
		return getExtension(f.getAbsolutePath());
	}

	public static String getExtension(String path) {
		if (null == path)
			return null;
		int pos = path.lastIndexOf('.');
		if (-1 == pos)
			return "";
		return path.substring(pos + 1);
	}

	public static ZipEntry[] findEntryInZip(ZipFile zip, String regex) {
		List<ZipEntry> list = new LinkedList<ZipEntry>();
		Enumeration<? extends ZipEntry> en = zip.entries();
		while (en.hasMoreElements()) {
			ZipEntry ze = en.nextElement();
			if (null == regex || ze.getName().matches(regex))
				list.add(ze);
		}
		return list.toArray(new ZipEntry[list.size()]);
	}

	public static File findFile(String path, Class<Files> klass, String enc) {
		File f = new File(path);
		if (!f.exists()) {
			f = null;
			URL url = null;
			if (null != klass) {
				url = klass.getResource(path);
				if (null == url)
					url = klass.getClassLoader().getResource(path);
			}
			if (null == url)
				url = ClassLoader.getSystemResource(path);
			if (null != url) {
				try {
					f = new File(URLDecoder.decode(url.getPath(), enc));
				} catch (UnsupportedEncodingException e) {
					f = null;
				}
			}
		}
		return f;
	}

	public static File findFile(String path, String enc) {
		return findFile(path, Files.class, enc);
	}

	public static File findFile(String path, Class<Files> klass) {
		return findFile(path, klass, Charset.defaultCharset().name());
	}

	public static File findFile(String path) {
		return findFile(path, Files.class, Charset.defaultCharset().name());
	}

	public static InputStream findFileAsStream(String path, Class<Files> klass, String enc) {
		File f = new File(path);
		if (f.exists())
			try {
				return new FileInputStream(f);
			} catch (FileNotFoundException e1) {
				return null;
			}
		URL url = null;
		if (null != klass) {
			InputStream ins = klass.getResourceAsStream(path);
			if (null != ins)
				return ins;
		}
		if (null == url)
			url = ClassLoader.getSystemResource(path);
		if (null != url) {
			try {
				return url.openStream();
			} catch (IOException e) {
				return null;
			}
		}
		return null;
	}

	public static InputStream findFileAsStream(String path, String enc) {
		return findFileAsStream(path, Files.class, enc);
	}

	public static InputStream findFileAsStream(String path, Class<Files> klass) {
		return findFileAsStream(path, klass, Charset.defaultCharset().name());
	}

	public static InputStream findFileAsStream(String path) {
		return findFileAsStream(path, Files.class, Charset.defaultCharset().name());
	}

	public static boolean isDirectory(File dir) {
		if (null == dir)
			return false;
		if (!dir.exists())
			return false;
		if (!dir.isDirectory())
			return false;
		return true;
	}

	public static boolean isFile(File f) {
		if (null == f)
			return false;
		if (!f.exists())
			return false;
		if (!f.isFile())
			return false;
		return true;
	}

	public static boolean createNewFile(File f) throws IOException {
		if (f.exists())
			return false;
		makeDir(f.getParentFile());
		return f.createNewFile();
	}

	public static boolean makeDir(File dir) throws IOException {
		if (dir.exists())
			return false;
		return dir.mkdirs();
	}

	public static boolean deleteDir(File dir) throws IOException {
		if (!dir.exists())
			return false;
		if (!dir.isDirectory())
			throw new IOException("\"" + dir.getAbsolutePath() + "\" should be a directory!");
		File[] files = dir.listFiles();
		if (files.length == 0)
			return dir.delete();

		boolean re = false;
		for (File f : files) {
			if (f.isDirectory())
				re |= deleteDir(f);
			else
				re |= deleteFile(f);
		}
		return dir.delete();
	}

	public static boolean deleteFile(File f) {
		return f.delete();
	}

	public static boolean clearDir(File dir) throws IOException {
		if (!dir.exists())
			return false;
		if (deleteDir(dir))
			return makeDir(dir);
		return false;
	}

	public static File getFileById(int fId, File home, String extension) {
		String fs = String.format("%08X", fId);
		StringBuffer sb = new StringBuffer(home.getAbsolutePath());
		for (int i = 0; i < 8; i += 2) {
			sb.append("/" + fs.substring(i, i + 2));
		}
		if (!Strings.isBlank(extension))
			sb.append("." + extension);
		File f = new File(sb.toString());
		return f;
	}

	public static String getFilePathById(int fId, String home, String extension) {
		String fs = String.format("%08X", fId);
		StringBuffer sb = new StringBuffer(home);
		for (int i = 0; i < 8; i += 2) {
			sb.append("/" + fs.substring(i, i + 2));
		}
		if (!Strings.isBlank(extension))
			sb.append("." + extension);
		return sb.toString();
	}

	public static boolean copyFile(File src, File target) throws IOException {
		if (!src.exists())
			return false;
		if (!target.exists())
			if (!createNewFile(target))
				return false;
		InputStream ins = new BufferedInputStream(new FileInputStream(src));
		OutputStream ops = new BufferedOutputStream(new FileOutputStream(target));
		int b;
		while (-1 != (b = ins.read()))
			ops.write(b);

		ins.close();
		ops.close();
		target.setLastModified(src.lastModified());
		return true;
	}

	public static boolean copyFolder(File src, File target) throws IOException {
		if (!src.exists())
			return false;
		if (!src.isDirectory())
			throw new IOException(src.getAbsolutePath() + " should be a directory!");
		if (!target.exists())
			if (!makeDir(target))
				return false;
		boolean re = true;
		File[] files = src.listFiles();
		for (int i = 0; i < files.length; i++) {
			File f = files[i];
			if (f.isFile())
				re &= copyFile(files[i], new File(target.getAbsolutePath() + "/" + f.getName()));
			else
				re &= copyFolder(files[i], new File(target.getAbsolutePath() + "/" + f.getName()));
		}
		return re;
	}

	public static boolean moveTo(File src, File target) throws IOException {
		makeDir(target.getParentFile());
		return src.renameTo(target);
	}

	public static void cleanAllFolderInSubFolderes(File dir, String name) throws IOException {
		File[] files = dir.listFiles();
		for (int i = 0; i < files.length; i++) {
			File d = files[i];
			if (d.isDirectory())
				if (d.getName().equalsIgnoreCase(name))
					deleteDir(d);
				else
					cleanAllFolderInSubFolderes(d, name);
		}
	}

	public static boolean isEquals(File f1, File f2) {
		if (!f1.isFile() || !f2.isFile())
			return false;
		InputStream ins1 = null;
		InputStream ins2 = null;
		try {
			ins1 = new BufferedInputStream(new FileInputStream(f1));
			ins2 = new BufferedInputStream(new FileInputStream(f2));
			return Streams.isEquals(ins1, ins2);
		} catch (Exception e) {
			return false;
		} finally {
			Streams.safeClose(ins1);
			Streams.safeClose(ins2);
		}
	}
}
