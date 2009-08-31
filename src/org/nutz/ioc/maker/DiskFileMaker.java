package org.nutz.ioc.maker;

import java.io.File;
import java.io.FileNotFoundException;

import org.nutz.ioc.ValueMaker;
import org.nutz.ioc.meta.Val;
import org.nutz.lang.Files;

public class DiskFileMaker implements ValueMaker {

	public String forType() {
		return Val.disk;
	}

	public Object make(Val val) {
		String path = val.getValue().toString();
		File re = Files.findFile(path);
		if (null == re || !re.exists())
			throw new RuntimeException(new FileNotFoundException(path));
		return re;
	}

}
