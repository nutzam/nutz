package com.zzh.mvc;

public interface MvcSupport {

	Url getUrl(String path) throws UrlNotFoundException;

}
