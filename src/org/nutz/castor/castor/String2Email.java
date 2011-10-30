package org.nutz.castor.castor;

import java.lang.reflect.Type;

import org.nutz.castor.Castor;
import org.nutz.lang.meta.Email;

public class String2Email extends Castor<String, Email> {

	@Override
	public Email cast(String src, Type toType, String... args) {
		return new Email(src);
	}

}
