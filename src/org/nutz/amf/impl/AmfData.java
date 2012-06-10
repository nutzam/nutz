package org.nutz.amf.impl;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.util.ArrayList;
import java.util.List;

public class AmfData {

	public int version;
	public List<AmfHeader> headers = new ArrayList<AmfHeader>();
	public List<AmfMessage> msgs = new ArrayList<AmfMessage>();
	
	public void readObject(DataInputStream din) {
		
	}
	
	public void writeObject(DataOutputStream dout) {
		
	}
}
