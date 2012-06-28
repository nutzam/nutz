package org.nutz.web;


public class NutHttpSession extends HttpObject {
    
	protected String id;
	public String getId() {
		return id;
	}

	protected long creationTime = System.currentTimeMillis();
	public long getCreationTime() {
		return creationTime;
	}

	protected long lastAccessedTime = System.currentTimeMillis();
	
	public long getLastAccessedTime() {
		return lastAccessedTime;
	}

	protected int maxInactiveInterval = 30;
	
	public int getMaxInactiveInterval() {
		return maxInactiveInterval;
	}
	
	public void setMaxInactiveInterval(int maxInactiveInterval) {
		this.maxInactiveInterval = maxInactiveInterval;
	}

	
	public void invalidate() {
		sessionManger.kill(this);
	}

	protected boolean _new;
	
	public boolean isNew() {
		return _new;
	}
}
