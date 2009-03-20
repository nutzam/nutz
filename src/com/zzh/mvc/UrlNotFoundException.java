package com.zzh.mvc;

@SuppressWarnings("serial")
public class UrlNotFoundException extends Exception {

	public UrlNotFoundException(String path) {
		super(String.format("Fail to match url [%s]", path));
	}

}
