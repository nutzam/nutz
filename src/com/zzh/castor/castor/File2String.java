package com.zzh.castor.castor;

import java.io.File;

import com.zzh.castor.Castor;
import com.zzh.castor.FailToCastObjectException;

public class File2String extends Castor<File, String> {

	@Override
	protected String cast(File src, Class<?> toType, String... args)
			throws FailToCastObjectException {
		return src.getAbsolutePath();
	}

}
