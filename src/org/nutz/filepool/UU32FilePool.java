package org.nutz.filepool;

import org.nutz.lang.Files;
import org.nutz.lang.Lang;
import org.nutz.lang.random.R;

import java.io.File;

public class UU32FilePool implements FilePool {
	
	File root;
	
	public UU32FilePool(String path) {
		this.root = Files.createDirIfNoExists(root);
	}

	public File createFile(String suffix) {
		String key = R.UU32();
		File dir = new File(root, key.substring(0, 2));
		Files.createDirIfNoExists(dir);
		return new File(dir, key.substring(2));
	}
	public void clear() {
		Files.deleteDir(root);
		this.root = Files.createDirIfNoExists(root);
	}
	
	//-----------------------------
	// 其他方法一概不实现
	//-----------------------------

	public long current() {
		throw Lang.noImplement();
	}

	public boolean hasFile(long fId, String suffix) {
		throw Lang.noImplement();
	}

	@Override
	public File removeFile(long fId, String suffix) {
		throw Lang.noImplement();
	}

	public long getFileId(File f) {
		throw Lang.noImplement();
	}

	public File getFile(long fId, String suffix) {
		throw Lang.noImplement();
	}

	public File returnFile(long fId, String suffix) {
		throw Lang.noImplement();
	}

	public boolean hasDir(long fId) {
		throw Lang.noImplement();
	}

	public File removeDir(long fId) {
		throw Lang.noImplement();
	}

	public File createDir() {
		throw Lang.noImplement();
	}

	public File getDir(long fId) {
		throw Lang.noImplement();
	}

	public File returnDir(long fId) {
		throw Lang.noImplement();
	}


}
