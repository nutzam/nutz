package com.zzh.castor.castor;

import java.io.File;

import com.zzh.castor.Castor;
import com.zzh.castor.FailToCastObjectException;
import com.zzh.lang.Files;

public class String2File extends Castor<String, File> {

	@Override
	protected File cast(String src, Class<?> toType, String... args)
			throws FailToCastObjectException {
		return Files.findFile(src);
	}

}
