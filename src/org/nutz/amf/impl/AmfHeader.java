package org.nutz.amf.impl;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Map;

public class AmfHeader {

	public int version;
	public String name;
	public boolean mustUnderstand;
	public Object body;
	
	public void readObject(DataInputStream dis) throws IOException {
		name = AmfParser.readUTF(dis);
		mustUnderstand = dis.readBoolean();
		dis.readInt(); // skip body size
		
		Map<String, Object> res = AmfParser.readAmfObject(dis);
		body = res.get("body");
		if (res.containsKey("version"))
			version = (Integer)res.get("version");
	}
	
	public void writeObject(DataOutputStream dout) throws IOException {
		dout.writeUTF(name);
		dout.writeBoolean(mustUnderstand);
		AmfParser.writeBodyObject(body, version, dout);
	}
}
