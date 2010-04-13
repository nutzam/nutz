package org.nutz.mock.servlet.multipart.item;

import org.nutz.mock.servlet.multipart.MultipartItem;
import org.nutz.mock.servlet.multipart.inputing.Inputings;

public class ParamMultipartItem extends MultipartItem {

	public ParamMultipartItem(String boundary, String name, String value) {
		super(boundary + "\r\n");
		addInputing(Inputings.name(name));
		addInputing(Inputings.blankLine());
		addInputing(Inputings.data(value));
		addInputing(Inputings.blankLine());
	}

}
