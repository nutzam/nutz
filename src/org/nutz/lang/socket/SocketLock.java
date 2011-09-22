package org.nutz.lang.socket;

/**
 * 一个端口的监听，利用这个对象来获知是不是需要停止监听了
 * 
 * @author zozoh(zozohtnt@gmail.com)
 */
public class SocketLock {

	private boolean stop;

	public synchronized void setStop(boolean stop) {
		this.stop = stop | this.stop;//如果已经设置为stop,则不允许取消
	}

	public synchronized boolean isStop() {
		return stop;
	}

}
