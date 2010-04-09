package org.nutz.filepool;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;

import org.nutz.lang.Files;
import org.nutz.lang.Lang;

public class NutFilePool implements FilePool {

	public NutFilePool(String homePath) {
		this(homePath, 0);
	}

	public NutFilePool(String homePath, long size) {
		this.size = size;
		try {
			home = Files.createDirIfNoExists(homePath);
		}
		catch (IOException e) {
			throw Lang.wrapThrow(e);
		}
		if (null == home) {
			home = new File(homePath);
			Files.makeDir(home);
		}
		if (!home.isDirectory())
			throw Lang.makeThrow(	"Path error '%s'! ,You must declare a real directory as the '%s' home folder.",
									homePath,
									this.getClass().getName());
		File last = home;
		String[] subs = null;
		while (last.isDirectory()) {
			subs = last.list(new FilenameFilter() {

				public boolean accept(File dir, String name) {
					return name.matches("^([\\d|A-F]{2})([.][a-zA-Z]{1,})?$");
				}
			});
			if (null != subs && subs.length > 0) {
				last = new File(last.getAbsolutePath() + "/" + subs[subs.length - 1]);
				if (last.isFile()) {
					cursor = Pools.getFileId(home, last);
					break;
				}
			} else {
				break;
			}
		}
	}

	private File home;
	private long cursor;
	private long size;

	public void clear() {
		try {
			Files.deleteDir(home);
			Files.makeDir(home);
			cursor = 0;
		}
		catch (IOException e) {
			throw Lang.wrapThrow(e);
		}
	}

	public File createFile(String suffix) {
		if (size > 0 && cursor >= size)
			cursor = -1;
		long id = ++cursor;
		if (size > 0 && id >= size)
			Lang.makeThrow("Id (%d) is out of range (%d)", id, size);
		File re = Pools.getFileById(home, id, suffix);
		if (!re.exists())
			try {
				Files.createNewFile(re);
			}
			catch (IOException e) {
				throw Lang.wrapThrow(e);
			}
		return re;
	}

	public long current() {
		return cursor;
	}

	public long getFileId(File f) {
		try {
			return Pools.getFileId(home, f);
		}
		catch (Exception e) {
			return -1;
		}
	}

	public File removeFile(long fId, String suffix) {
		File f = Pools.getFileById(home, fId, suffix);
		Files.deleteFile(f);
		return f;
	}

	public boolean hasFile(long fId, String suffix) {
		File f = Pools.getFileById(home, fId, suffix);
		return f.exists();
	}

	public File getFile(long fId, String suffix) {
		return Pools.getFileById(home, fId, suffix);
	}

}
