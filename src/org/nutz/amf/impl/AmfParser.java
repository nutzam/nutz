package org.nutz.amf.impl;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PushbackInputStream;
import java.util.HashMap;
import java.util.Map;

import org.nutz.lang.Lang;
import org.nutz.lang.Streams;
import org.nutz.log.Log;
import org.nutz.log.Logs;

public class AmfParser {
	
	private static final Log log = Logs.get();

	public AmfData decode(InputStream in) throws IOException {
		DataInputStream dis = null;
		if (in instanceof DataInputStream)
			dis = (DataInputStream)dis;
		else
			dis = new DataInputStream(in);
		
		//对其版本号
		AmfData amf = new AmfData();
		amf.version = dis.readUnsignedShort();
		
		int headerCount = dis.readUnsignedShort();
		for (int i = 0; i < headerCount; i++) {
			AmfHeader header = new AmfHeader();
			header.readObject(dis);
			amf.headers.add(header);
		}
		
		int messageCount = dis.readUnsignedShort();
		for (int i = 0; i < messageCount; i++) {
			AmfMessage msg = new AmfMessage();
			msg.readObject(dis);
			amf.msgs.add(msg);
		}
		return amf;
	}
	
	public static Map<String, Object> readObject(DataInputStream dis) throws IOException {
		PushbackInputStream pis = new PushbackInputStream(dis, 1);
		Map<String, Object> map = new HashMap<String, Object>();
		int b = pis.read();
		int version = 0;
		if (b == 0x17) {
			version = 3;
		}
		map.put("version", version);
		pis.unread(b);
		
		if (version == 0) {
			map.put("body", new Amf0Parser().readObject(new DataInputStream(pis)));
		} else {
			map.put("body", new Amf3Parser().readObject(new DataInputStream(pis)));
		}
		return map;
	}
	
	public static void writeObject(Object obj, int version, DataOutputStream dout) throws IOException {
		if (version == 0)
			new Amf0Parser().writeObject(dout);
		else
			new Amf3Parser().writeObject(dout);
	}
	
	public static void writeBodyObject(Object body, int version, DataOutputStream dout) throws IOException {
		File tmp = File.createTempFile("nutz.amf.", ".data");
		FileOutputStream fos = new FileOutputStream(tmp);
		DataOutputStream dos = new DataOutputStream(fos);
		AmfParser.writeObject(body, version, dos);
		dos.flush();
		dos.close();
		
		FileInputStream fis = new FileInputStream(tmp);
		dout.writeInt((int) tmp.length());
		Streams.write(dout, fis);
		fis.close();
		tmp.delete();
	}
	
	static class Amf0Parser {
		public Object readObject(DataInputStream dis) throws IOException {
			int typeId = dis.read();
			switch (typeId) {
			case 0:
				return dis.readFloat();
			case 1:
				return dis.readBoolean();
			case 2:
				int len = dis.readUnsignedShort();
				byte[] buf = new byte[len];
				dis.read(buf);
				return new String(buf, "UTF-8");
			case 3:
				return readMap(dis); //Object
			case 5:
			case 6:
				return null;// null and Undefined
			case 7:
				throw Lang.noImplement(); //refer
			case 8:
				throw Lang.noImplement(); //array
			case 9:
				throw Lang.noImplement();//impossible!
			case 10:
				throw Lang.noImplement();//String array
			case 11:
				throw Lang.noImplement();//Date
			case 12:
				throw Lang.noImplement();//Long String
				
			default:
				throw Lang.noImplement();
			}
			
		}
		
		public Map readMap(DataInputStream dis) {
			return null;
		}
		
		public void writeObject(DataOutputStream dout) throws IOException {
			
		}
	}
	
	static class Amf3Parser {

		public Object readObject(DataInputStream dis) throws IOException {
			throw Lang.noImplement();
		}
		
		public void writeObject(DataOutputStream dout) throws IOException {
			
		}
	}
}
