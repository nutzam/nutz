package org.nutz.web;



public interface SessionManger {

	NutHttpSession get(NutHttpReq req, boolean createIfNotExist);
	
	void kill(NutHttpSession nutHttpSession);
}
