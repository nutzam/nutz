package org.nutz.http.impl;

public interface NutHttpAction {

	void exec(NutHttpReq req, NutHttpResp resp);
	
	boolean canWork(NutHttpReq req);
}
