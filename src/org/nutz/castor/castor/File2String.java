package org.nutz.castor.castor;

import java.io.File;
import java.lang.reflect.Type;

import org.nutz.castor.Castor;
import org.nutz.castor.FailToCastObjectException;

public class File2String extends Castor<File, String> {

	@Override
	public String cast(File src, Type toType, String... args) throws FailToCastObjectException {
		return src.getAbsolutePath();
	}

}
