package com.zzh.ioc.maker;

import java.io.File;
import java.io.FileNotFoundException;

import com.zzh.ioc.ValueMaker;
import com.zzh.ioc.meta.Val;
import com.zzh.lang.Files;

public class DiskFileMaker implements ValueMaker {

	@Override
	public String forType() {
		return Val.disk;
	}

	@Override
	public Object make(Val val) {
		String path = val.getValue().toString();
		File re = Files.findFile(path);
		if (null == re || !re.exists())
			throw new RuntimeException(new FileNotFoundException(path));
		return re;
	}

}
