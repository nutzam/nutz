package com.zzh.ioc;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Map;

import com.zzh.lang.Files;

public class DiskFileMaker extends ObjectMaker {

	@Override
	protected boolean accept(Map<String, Object> properties) {
		return properties.containsKey("disk");
	}

	@Override
	protected Object make(Map<String, Object> properties) {
		String path = properties.get("disk").toString();
		File re = Files.findFile(path);
		if (null == re || !re.exists())
			throw new RuntimeException(new FileNotFoundException(path));
		return re;
	}

}
