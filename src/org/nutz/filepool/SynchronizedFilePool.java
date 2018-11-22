package org.nutz.filepool;

import java.io.File;

/**
 * 将FilePool封装为线程同步的
 * @author wendal
 *
 */
public class SynchronizedFilePool implements FilePool {
	
	private FilePool proxy;
	
	public SynchronizedFilePool(FilePool proxy) {
		this.proxy = proxy;
	}

	public synchronized long current() {
		return proxy.current();
	}

	public synchronized boolean hasFile(long fId, String suffix) {
		return proxy.hasFile(fId, suffix);
	}

	public synchronized File removeFile(long fId, String suffix) {
		return proxy.removeFile(fId, suffix);
	}

	public synchronized File createFile(String suffix) {
		return proxy.createFile(suffix);
	}

	public synchronized long getFileId(File f) {
		return proxy.getFileId(f);
	}

	public synchronized File getFile(long fId, String suffix) {
		return proxy.getFile(fId, suffix);
	}

	public synchronized File returnFile(long fId, String suffix) {
		return proxy.returnFile(fId, suffix);
	}

	public synchronized boolean hasDir(long fId) {
		return proxy.hasDir(fId);
	}

	public synchronized File removeDir(long fId) {
		return proxy.removeDir(fId);
	}

	public synchronized File createDir() {
		return proxy.createDir();
	}

	public synchronized File getDir(long fId) {
		return proxy.getDir(fId);
	}

	public synchronized File returnDir(long fId) {
		return proxy.returnDir(fId);
	}

	public synchronized void clear() {
		proxy.clear();
	}

}
