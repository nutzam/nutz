package org.nutz.mvc;

public interface MvcSupport {

	Url getUrl(String path) throws UrlNotFoundException;

}
