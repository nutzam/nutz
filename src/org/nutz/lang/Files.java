package org.nutz.lang;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileFilter;
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

/**
 * 文件操作的帮助函数
 * 
 * @author amos(amosleaf@gmail.com)
 * @author zozoh(zozohtnt@gmail.com)
 * @author wendal(wendal1985@gmail.com)
 */
public abstract class Files {

	/**
	 * 读取 UTF-8 文件全部内容
	 * 
	 * @param path
	 *            文件路径
	 * @return 文件内容
	 */
	public static String read(String path) {
		File f = Files.findFile(path);
		if (null == f)
			throw Lang.makeThrow("Can not find file '%s'", path);
		return read(f);
	}

	/**
	 * 读取 UTF-8 文件全部内容
	 * 
	 * @param f
	 *            文件
	 * @return 文件内容
	 */
	public static String read(File f) {
		return Lang.readAll(Streams.fileInr(f));
	}

	/**
	 * 将内容写入 UTF-8 文件，如文件不存在，创建这个文件
	 * 
	 * @param path
	 *            文件路径
	 * @param content
	 *            内容
	 */
	public static void write(String path, Object content) {
		if (null == path || null == content)
			return;
		File f = Files.findFile(path);
		if (null == f) {
			f = new File(path);
			try {
				Files.createNewFile(f);
			} catch (IOException e) {
				throw Lang.wrapThrow(e);
			}
		}
		Lang.writeAll(Streams.fileOutw(f), content.toString());
	}

	/**
	 * 将内容写入 UTF-8 文件，如文件不存在，创建这个文件
	 * 
	 * @param f
	 *            文件
	 * @param content
	 *            内容
	 */
	public static void write(File f, Object content) {
		if (null == f || null == content)
			return;
		if (f.isDirectory())
			throw Lang.makeThrow("Directory '%s' can not be write as File", f);
		try {
			if (!f.exists())
				Files.createNewFile(f);
			Lang.writeAll(Streams.fileOutw(f), content.toString());
		} catch (IOException e) {
			throw Lang.wrapThrow(e);
		}
	}

	/**
	 * 将文件后缀改名，从而生成一个新的文件对象。但是并不在磁盘上创建它
	 * 
	 * @param f
	 *            文件
	 * @param suffix
	 *            新后缀
	 * @return 新文件对象
	 */
	public static File renameSuffix(File f, String suffix) {
		if (null == f)
			return null;
		if (null == suffix || suffix.length() == 0)
			return f;
		return new File(renameSuffix(f.getName(), suffix));
	}

	/**
	 * 将文件路径后缀改名，从而生成一个新的文件路径。
	 * 
	 * @param path
	 *            文件路径
	 * @param suffix
	 *            新后缀
	 * @return 新文件后缀
	 */
	public static String renameSuffix(String path, String suffix) {
		int pos = path.length();
		for (--pos; pos > 0; pos--) {
			if (path.charAt(pos) == '.')
				break;
			if (path.charAt(pos) == '/' || path.charAt(pos) == '\\') {
				pos = -1;
				break;
			}
		}
		if (0 >= pos)
			return path + suffix;
		return path.substring(0, pos) + suffix;
	}

	/**
	 * 获取文件主名。 即去掉后缀的名称
	 * 
	 * @param path
	 *            文件路径
	 * @return 文件主名
	 */
	public static String getMajorName(String path) {
		int len = path.length();
		int l = 0;
		int r = len;
		for (int i = r - 1; i > 0; i--) {
			if (r == len)
				if (path.charAt(i) == '.') {
					r = i;
				}
			if (path.charAt(i) == '/' || path.charAt(i) == '\\') {
				l = i + 1;
				break;
			}
		}
		return path.substring(l, r);
	}

	/**
	 * 获取文件主名。 即去掉后缀的名称
	 * 
	 * @param f
	 *            文件
	 * @return 文件主名
	 */
	public static String getMajorName(File f) {
		return getMajorName(f.getAbsolutePath());
	}

	/**
	 * 获取文件后缀名，不包括 '.'，如 'abc.gif','，则返回 'gif'
	 * 
	 * @param f
	 *            文件
	 * @return 文件后缀名
	 */
	public static String getSuffixName(File f) {
		if (null == f)
			return null;
		return getSuffixName(f.getAbsolutePath());
	}

	/**
	 * 获取文件后缀名，不包括 '.'，如 'abc.gif','，则返回 'gif'
	 * 
	 * @param path
	 *            文件路径
	 * @return 文件后缀名
	 */
	public static String getSuffixName(String path) {
		if (null == path)
			return null;
		int pos = path.lastIndexOf('.');
		if (-1 == pos)
			return "";
		return path.substring(pos + 1);
	}

	/**
	 * 根据正则式，从压缩文件中获取文件
	 * 
	 * @param zip
	 *            压缩文件
	 * @param regex
	 *            正则式，用来匹配文件名
	 * @return 数组
	 */
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

	/**
	 * 从 CLASSPATH 下寻找一个文件
	 * 
	 * @param path
	 *            文件路径
	 * @param klass
	 *            参考的类， -- 会用这个类的 ClassLoader
	 * @param enc
	 *            文件路径编码
	 * 
	 * @return 文件对象，如果不存在，则为 null
	 */
	public static File findFile(String path, Class<?> klass, String enc) {
		if (null == path)
			return null;
		try {
			path = URLDecoder.decode(path, Charset.defaultCharset().name());
		} catch (UnsupportedEncodingException e) {}
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

	/**
	 * 从 CLASSPATH 下寻找一个文件
	 * 
	 * @param path
	 *            文件路径
	 * @param enc
	 *            文件路径编码
	 * @return 文件对象，如果不存在，则为 null
	 */
	public static File findFile(String path, String enc) {
		return findFile(path, Files.class, enc);
	}

	/**
	 * 从 CLASSPATH 下寻找一个文件
	 * 
	 * @param path
	 *            文件路径
	 * @param klass
	 *            参考的类， -- 会用这个类的 ClassLoader
	 * 
	 * @return 文件对象，如果不存在，则为 null
	 */
	public static File findFile(String path, Class<?> klass) {
		return findFile(path, klass, Charset.defaultCharset().name());
	}

	/**
	 * 从 CLASSPATH 下寻找一个文件
	 * 
	 * @param path
	 *            文件路径
	 * 
	 * @return 文件对象，如果不存在，则为 null
	 */
	public static File findFile(String path) {
		return findFile(path, Files.class, Charset.defaultCharset().name());
	}

	/**
	 * 获取输出流
	 * 
	 * @param path
	 *            文件路径
	 * @param klass
	 *            参考的类， -- 会用这个类的 ClassLoader
	 * @param enc
	 *            文件路径编码
	 * 
	 * @return 输出流
	 */
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

	/**
	 * 获取输出流
	 * 
	 * @param path
	 *            文件路径
	 * @param enc
	 *            文件路径编码
	 * 
	 * @return 输出流
	 */
	public static InputStream findFileAsStream(String path, String enc) {
		return findFileAsStream(path, Files.class, enc);
	}

	/**
	 * 获取输出流
	 * 
	 * @param path
	 *            文件路径
	 * @param klass
	 *            参考的类， -- 会用这个类的 ClassLoader
	 * 
	 * @return 输出流
	 */
	public static InputStream findFileAsStream(String path, Class<Files> klass) {
		return findFileAsStream(path, klass, Charset.defaultCharset().name());
	}

	/**
	 * 获取输出流
	 * 
	 * @param path
	 *            文件路径
	 * 
	 * @return 输出流
	 */
	public static InputStream findFileAsStream(String path) {
		return findFileAsStream(path, Files.class, Charset.defaultCharset().name());
	}

	/**
	 * 文件对象是否是目录，可接受 null
	 */
	public static boolean isDirectory(File f) {
		if (null == f)
			return false;
		if (!f.exists())
			return false;
		if (!f.isDirectory())
			return false;
		return true;
	}

	/**
	 * 文件对象是否是文件，可接受 null
	 */
	public static boolean isFile(File f) {
		if (null == f)
			return false;
		if (!f.exists())
			return false;
		if (!f.isFile())
			return false;
		return true;
	}

	/**
	 * 创建新文件，如果父目录不存在，也一并创建。可接受 null 参数
	 * 
	 * @param f
	 *            文件对象
	 * @return false，如果文件已存在。 true 创建成功
	 * @throws IOException
	 */
	public static boolean createNewFile(File f) throws IOException {
		if (null == f)
			return false;
		if (f.exists())
			return false;
		makeDir(f.getParentFile());
		return f.createNewFile();
	}

	/**
	 * 创建新目录，如果父目录不存在，也一并创建。可接受 null 参数
	 * 
	 * @param dir
	 *            目录对象
	 * @return false，如果目录已存在。 true 创建成功
	 * @throws IOException
	 */
	public static boolean makeDir(File dir) throws IOException {
		if (null == dir)
			return false;
		if (dir.exists())
			return false;
		return dir.mkdirs();
	}

	/**
	 * 强行删除一个目录，保括这个目录下所有的子目录和文件
	 * 
	 * @param dir
	 *            目录
	 * @return 是否删除成功
	 * @throws IOException
	 */
	public static boolean deleteDir(File dir) throws IOException {
		if (null == dir)
			return false;
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
		re |= dir.delete();
		return re;
	}

	/**
	 * 删除一个文件
	 * 
	 * @param f
	 *            文件
	 * @return 是否删除成功
	 * @throws IOException
	 */
	public static boolean deleteFile(File f) {
		if (null == f)
			return false;
		return f.delete();
	}

	/**
	 * 清除一个目录里所有的内容
	 * 
	 * @param dir
	 *            目录
	 * @return 是否清除成功
	 * @throws IOException
	 */
	public static boolean clearDir(File dir) throws IOException {
		if (null == dir)
			return false;
		if (!dir.exists())
			return false;
		File[] fs = dir.listFiles();
		for (File f : fs) {
			if (f.isFile())
				Files.deleteFile(f);
			else if (f.isDirectory())
				Files.deleteDir(f);
		}
		return false;
	}

	/**
	 * 拷贝一个文件
	 * 
	 * @param src
	 *            原始文件
	 * @param target
	 *            新文件
	 * @return 是否拷贝成功
	 * @throws IOException
	 */
	public static boolean copyFile(File src, File target) throws IOException {
		if (src == null || target == null)
			return false;
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
		return target.setLastModified(src.lastModified());
	}

	/**
	 * 拷贝一个目录
	 * 
	 * @param src
	 *            原始目录
	 * @param target
	 *            新目录
	 * @return 是否拷贝成功
	 * @throws IOException
	 */
	public static boolean copyDir(File src, File target) throws IOException {
		if (src == null || target == null)
			return false;
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
				re &= copyDir(files[i], new File(target.getAbsolutePath() + "/" + f.getName()));
		}
		return re;
	}

	/**
	 * 将文件移动到新的位置
	 * 
	 * @param src
	 *            原始文件
	 * @param target
	 *            新文件
	 * @return 移动是否成功
	 * @throws IOException
	 */
	public static boolean move(File src, File target) throws IOException {
		if (src == null || target == null)
			return false;
		makeDir(target.getParentFile());
		return src.renameTo(target);
	}

	/**
	 * 将文件改名
	 * 
	 * @param src
	 *            文件
	 * @param newName
	 *            新名称
	 * @return 改名是否成功
	 */
	public static boolean rename(File src, String newName) {
		if (src == null || newName == null)
			return false;
		if (src.exists()) {
			File newFile = new File(src.getParent() + "/" + newName);
			if (newFile.exists())
				return false;
			try {
				Files.makeDir(newFile.getParentFile());
			} catch (IOException e) {
				return false;
			}
			return src.renameTo(newFile);
		}
		return false;
	}

	/**
	 * 将一个目录下的特殊名称的目录彻底删除，比如 '.svn' 或者 '.cvs'
	 * 
	 * @param dir
	 *            目录
	 * @param name
	 *            要清除的目录名
	 * @throws IOException
	 */
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

	/**
	 * 精确比较两个文件是否相等
	 * 
	 * @param f1
	 *            文件1
	 * @param f2
	 *            文件2
	 * @return 是否相等
	 */
	public static boolean isEquals(File f1, File f2) {
		if (!f1.isFile() || !f2.isFile())
			return false;
		InputStream ins1 = null;
		InputStream ins2 = null;
		try {
			ins1 = new BufferedInputStream(new FileInputStream(f1));
			ins2 = new BufferedInputStream(new FileInputStream(f2));
			return Streams.equals(ins1, ins2);
		} catch (Exception e) {
			return false;
		} finally {
			Streams.safeClose(ins1);
			Streams.safeClose(ins2);
		}
	}

	/**
	 * 在一个目录下，获取一个文件对象
	 * 
	 * @param dir
	 *            目录
	 * @param path
	 *            文件相对路径
	 * @return 文件
	 */
	public static File getFile(File dir, String path) {
		if (dir.exists()) {
			if (dir.isDirectory())
				return new File(dir.getAbsolutePath() + "/" + path);
			return new File(dir.getParent() + "/" + path);
		}
		return new File(path);
	}

	/**
	 * 获取一个目录下所有子目录。子目录如果以 '.' 开头，将被忽略
	 * 
	 * @param dir
	 *            目录
	 * @return 子目录数组
	 */
	public static File[] dirs(File dir) {
		return dir.listFiles(new FileFilter() {
			public boolean accept(File f) {
				if (f.isHidden())
					return false;
				if (f.isDirectory())
					if (!f.getName().startsWith("."))
						return true;
				return false;
			}
		});
	}

	/**
	 * 获取一个目录下所有的文件。隐藏文件会被忽略。
	 * 
	 * @param dir
	 *            目录
	 * @param suffix
	 *            文件后缀名。如果为 null，则获取全部文件
	 * @return 文件数组
	 */
	public static File[] files(File dir, final String suffix) {
		return dir.listFiles(new FileFilter() {
			public boolean accept(File f) {
				if (f.isHidden())
					return false;
				if (f.isFile()) {
					if (null == suffix)
						return true;
					if (f.getName().endsWith(suffix))
						return true;
				}
				return false;
			}
		});
	}
}
