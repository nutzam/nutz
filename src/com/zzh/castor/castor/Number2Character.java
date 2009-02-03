package com.zzh.castor.castor;

import com.zzh.castor.Castor;

public class Number2Character extends Castor<Number, Character> {

	@Override
	protected Character cast(Number src, Class<?> toType) {
		return (char) src.intValue();
	}

}
