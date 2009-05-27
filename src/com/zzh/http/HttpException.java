package com.zzh.http;

@SuppressWarnings("serial")
public class HttpException extends RuntimeException {

	public HttpException(String url, Throwable cause) {
		super(cause);
	}

}
