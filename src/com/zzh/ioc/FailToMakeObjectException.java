package com.zzh.ioc;

public class FailToMakeObjectException extends RuntimeException {

	private static final long serialVersionUID = 5017414009775324162L;

	public FailToMakeObjectException(Throwable cause) {
		super(cause);
	}

}
