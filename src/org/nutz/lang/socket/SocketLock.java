package org.nutz.lang.socket;

/**
 * 一个端口的监听，利用这个对象来获知是不是需要停止监听了
 * 
 * @author zozoh(zozohtnt@gmail.com)
 */
public class SocketLock {

	private boolean stop;

	public synchronized void setStop(boolean stop) {
		this.stop = stop;
	}

	public synchronized boolean isStop() {
		return stop;
	}

}
