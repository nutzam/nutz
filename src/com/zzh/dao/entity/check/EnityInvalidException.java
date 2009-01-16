package com.zzh.dao.entity.check;

public class EnityInvalidException extends Exception {

	private static final long serialVersionUID = 7108838367868462001L;

	public EnityInvalidException(String message) {
		super(message);
	}

	private String messageKey;

	public EnityInvalidException(String key, String message) {
		super(message);
		this.messageKey = key;
	}

	public String getMessageKey() {
		return messageKey;
	}

}
