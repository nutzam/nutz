package org.nutz.web;


public interface NutHttpAction {

	void exec(NutHttpReq req, NutHttpResp resp);
	
	boolean canWork(NutHttpReq req);
}
