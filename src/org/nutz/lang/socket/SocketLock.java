package org.nutz.lang.socket;

/**
 * 一个端口的监听，利用这个对象来获知是不是需要停止监听了
 * 
 * @author zozoh(zozohtnt@gmail.com)
 */
public class SocketLock {

	private boolean stop;

	/**
	 * 一旦设置为true,就不允许取消
	 */
	public synchronized void setStop() {
		this.stop = true;
	}

	public synchronized boolean isStop() {
		return stop;
	}

}
