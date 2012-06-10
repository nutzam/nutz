package org.nutz.amf.impl;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Map;

import org.nutz.json.Json;

public class AmfMessage {

	public int version;
	public String targetURI;
	public String responseURI;
	public Object body;
	public void readObject(DataInputStream dis) throws IOException {
		targetURI = dis.readUTF();
		responseURI = dis.readUTF();
		dis.readInt(); //skip body size
		
		Map<String, Object> res = AmfParser.readObject(dis);
		body = res.get("body");
		if (res.containsKey("version"))
			version = (Integer)res.get("version");
	}
	
	public void writeObject(DataOutputStream dout) throws IOException {
		dout.writeUTF(targetURI);
		dout.writeUTF(responseURI);
		AmfParser.writeBodyObject(body, version, dout);
	}

	public String toString() {
		return "AmfMessage [version=" + version + ", targetURI=" + targetURI
				+ ", responseURI=" + responseURI + ", body=" + Json.toJson(body) + "]";
	}
}
