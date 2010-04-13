package org.nutz.mock.servlet.multipart.item;

import java.io.File;

import org.nutz.mock.servlet.multipart.MultipartItem;
import org.nutz.mock.servlet.multipart.inputing.Inputings;

public class FileMultipartItem extends MultipartItem {

	public FileMultipartItem(String boundary, String name, File f, String contentType) {
		super(boundary + "\r\n");
		addInputing(Inputings.fileName(name, f.getName()));
		addInputing(Inputings.contentType(contentType));
		addInputing(Inputings.blankLine());
		addInputing(Inputings.file(f));
		addInputing(Inputings.blankLine());
	}

}
