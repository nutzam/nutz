package org.nutz.rpc.json;

import org.nutz.lang.util.NutMap;

public class JsonRpcConfig {

	protected String endpoint;
	protected int timeout;
	protected NutMap namespaces;
	protected Object dft;
	
	public JsonRpcConfig() {
	}
	
	public JsonRpcConfig(String endpoint, int timeout, Object dft, NutMap namespaces) {
		super();
		this.endpoint = endpoint;
		this.timeout = timeout;
		this.dft = dft;
		this.namespaces = namespaces;
	}



	public String getEndpoint() {
		return endpoint;
	}
	public void setEndpoint(String endpoint) {
		this.endpoint = endpoint;
	}
	public int getTimeout() {
		return timeout;
	}
	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}
	public NutMap getNamespaces() {
		return namespaces;
	}
	public void setNamespaces(NutMap namespaces) {
		this.namespaces = namespaces;
	}
	public Object getDft() {
		return dft;
	}
	public void setDft(Object dft) {
		this.dft = dft;
	}
	
	
	
}
