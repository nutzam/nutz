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

	public NutFilePool(String homePath, int size) {
		this.size = size;
		home = Files.findFile(homePath);
		if (null == home) {
			home = new File(homePath);
			try {
				Files.makeDir(home);
			} catch (IOException e) {
				throw Lang.wrapThrow(e);
			}
		}
		if (!home.isDirectory())
			throw Lang.makeThrow(
					"Path error '%s'! ,You must declare a real directory as the '%s' home folder.",
					homePath, this.getClass().getName());
		File last = home;
		String[] subs = null;
		while (last.isDirectory()) {
			subs = last.list(new FilenameFilter(){
				@Override
				public boolean accept(File dir, String name) {
					return name.matches("^([\\d|A-F]{2})([.][a-zA-Z]{1,})?$");
				}
			});
			if (null != subs && subs.length > 0) {
				last = new File(last.getAbsolutePath() + "/" + subs[subs.length - 1]);
				if (last.isFile()) {
					cursor = Utils.getFileId(home, last);
					break;
				}
			} else {
				break;
			}
		}
	}

	private File home;
	private long cursor;
	private int size;

	@Override
	public void empty() throws IOException {
		Files.deleteDir(home);
		Files.makeDir(home);
		cursor = 0;
	}

	@Override
	public File createFile(long id, String suffix) throws IOException {
		if (size > 0 && id >= size)
			Lang.makeThrow("Id (%d) is out of range (%d)", id, size);
		File re = Utils.getFileById(home, id, suffix);
		if (!re.exists())
			Files.createNewFile(re);
		return re;
	}

	@Override
	public File createFile(String suffix) throws IOException {
		if (size > 0 && cursor >= size)
			cursor = -1;
		return createFile(++cursor, suffix);
	}

	@Override
	public long current() {
		return cursor;
	}

	@Override
	public File changeExtension(long id, String suffix, String newSuffix) {
		File f = Utils.getFileById(home, id, suffix);
		File newFile = Files.setSuffix(f, newSuffix);
		f.renameTo(newFile);
		return newFile;
	}

	@Override
	public long getFileId(File f) {
		try {
			return Utils.getFileId(home, f);
		} catch (Exception e) {
			return -1;
		}
	}

	@Override
	public File removeFile(long id, String suffix) {
		File f = Utils.getFileById(home, id, suffix);
		Files.deleteFile(f);
		return f;
	}

	@Override
	public boolean hasFile(long id, String suffix) {
		File f = Utils.getFileById(home, id, suffix);
		return f.exists();
	}

	@Override
	public File moveFile(long id, String suffix, File target) throws IOException {
		File src = Utils.getFileById(home, id, suffix);
		Files.moveTo(src, target);
		return target;
	}

}
