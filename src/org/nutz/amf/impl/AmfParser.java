package org.nutz.amf.impl;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.nutz.lang.Lang;
import org.nutz.lang.Streams;
import org.nutz.lang.Xmls;

public class AmfParser {

	public AmfData decode(InputStream in) throws IOException {
		DataInputStream dis = null;
		if (in instanceof DataInputStream)
			dis = (DataInputStream)dis;
		else
			dis = new DataInputStream(in);
		
		//查版本号
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
	
	public static Map<String, Object> readAmfObject(DataInputStream dis) throws IOException {
		Map<String, Object> map = new HashMap<String, Object>();
		int b = dis.read();
		int version = 0;
		if (b == 0x17) {
			version = 3;
		}
		map.put("version", version);
		
		if (version == 0) {
			map.put("body", new Amf0Parser().readAmf0Object(dis));
		} else {
			map.put("body", new Amf3Parser().readAmf3Object(dis));
		}
		return map;
	}
	
	public static void writeObject(Object obj, int version, DataOutputStream dout) throws IOException {
		if (version == 0)
			new Amf0Parser().writeAmf0Object(dout);
		else
			new Amf3Parser().writeAmf3Object(dout);
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
	
	public static String readUTF(DataInputStream dis) throws IOException {
		byte[] buf = new byte[dis.readUnsignedShort()];
		dis.read(buf);
		return new String(buf, "UTF-8");
	}
	
	static class Amf0Parser {
		public Object readAmf0Object(DataInputStream dis) throws IOException {
			int typeId = dis.read();
			switch (typeId) {
			case 0:
				return dis.readFloat();
			case 1:
				return dis.readBoolean();
			case 2:
				return readUTF(dis);
			case 8:
				dis.readInt(); //skip ecma array len
			case 3:
				Map<String, Object> map = new HashMap<String, Object>();
				int tid = dis.read();
				while (tid != 9) {
					map.put(readUTF(dis), readAmf0Object(dis));
					tid = dis.read();
				}
				return map; //Object
			case 5:
			case 6:
				return null;// null and Undefined
			case 7:
				throw Lang.noImplement(); //refer
			case 9: // End of Object
				throw Lang.noImplement();//impossible!
			case 10:
				ArrayList<Object> list = new ArrayList<Object>();
				int len = dis.readInt();
				for (int i = 0; i < len; i++) {
					list.add(readAmf0Object(dis));
				}
				return list;//Strict array
			case 11:
				dis.readUnsignedShort(); //skip timezone
				return new Date((long)(dis.readDouble() * 1000));//Date
			case 12:
				int len2 = dis.readInt();
				byte[] buf = new byte[len2];
				dis.read(buf);
				return new String(buf, "UTF-8");//Long String
			case 13:
				return null; //unsupport : 就叫这个名字,神啊!!
			case 15:
				int len3 = dis.readInt();
				byte[] buf3 = new byte[len3];
				dis.read(buf3);
				ByteArrayInputStream in = new ByteArrayInputStream(buf3);
				try {
					return Xmls.xmls().parse(in);
				} catch (Throwable e) {
					throw Lang.wrapThrow(e);
				} finally {
					in.close();
				}
			case 0x11:
				return new Amf3Parser().readAmf3Object(dis); //Switch to AMF3
			default:
				throw Lang.noImplement();
			}
		}
		
		public void writeAmf0Object(DataOutputStream dout) throws IOException {
			
		}
	}
	
	static class Amf3Parser {

		public Object readAmf3Object(DataInputStream dis) throws IOException {
			throw Lang.noImplement();
		}
		
		public void writeAmf3Object(DataOutputStream dout) throws IOException {
			
		}
	}
}
