package com.zzh.castor.castor;

import com.zzh.castor.Castor;
import com.zzh.lang.meta.Email;

public class String2Email extends Castor<String, Email> {

	@Override
	protected Email cast(String src, Class<?> toType, String... args) {
		return new Email(src);
	}

}
