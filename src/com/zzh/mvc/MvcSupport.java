package com.zzh.mvc;

public interface MvcSupport {

	/**
	 * @param url
	 * @return url mapping object
	 */
	public UrlMapping getUrlMapping(String url);

	/**
	 * @param name
	 *            : the name in DB entity and in configuration file
	 * @return Controllor Object
	 */
	public Controllor getControllor(String name);

	/**
	 * @param name
	 * @return View Object
	 */
	public View getView(String name);

	/**
	 * @param name
	 * @return Object
	 */
	public Object getService(String name);

}
