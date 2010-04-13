package org.nutz.mock.servlet.multipart.item;

import org.nutz.mock.servlet.multipart.MultipartItem;
import org.nutz.mock.servlet.multipart.inputing.StreamEndlInputing;

public class EndlMultipartItem extends MultipartItem {

	public EndlMultipartItem(String boundary) {
		super(boundary);
		this.addInputing(new StreamEndlInputing());
	}

}
