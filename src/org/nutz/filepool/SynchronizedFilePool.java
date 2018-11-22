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

	@Override
    public synchronized long current() {
		return proxy.current();
	}

	@Override
    public synchronized boolean hasFile(long fId, String suffix) {
		return proxy.hasFile(fId, suffix);
	}

	@Override
    public synchronized File removeFile(long fId, String suffix) {
		return proxy.removeFile(fId, suffix);
	}

	@Override
    public synchronized File createFile(String suffix) {
		return proxy.createFile(suffix);
	}

	@Override
    public synchronized long getFileId(File f) {
		return proxy.getFileId(f);
	}

	@Override
    public synchronized File getFile(long fId, String suffix) {
		return proxy.getFile(fId, suffix);
	}

	@Override
    public synchronized File returnFile(long fId, String suffix) {
		return proxy.returnFile(fId, suffix);
	}

	@Override
    public synchronized boolean hasDir(long fId) {
		return proxy.hasDir(fId);
	}

	@Override
    public synchronized File removeDir(long fId) {
		return proxy.removeDir(fId);
	}

	@Override
    public synchronized File createDir() {
		return proxy.createDir();
	}

	@Override
    public synchronized File getDir(long fId) {
		return proxy.getDir(fId);
	}

	@Override
    public synchronized File returnDir(long fId) {
		return proxy.returnDir(fId);
	}

	@Override
    public synchronized void clear() {
		proxy.clear();
	}

}
