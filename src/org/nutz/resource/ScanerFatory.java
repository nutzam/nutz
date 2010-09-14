package org.nutz.resource;

import javax.servlet.ServletContext;

import org.nutz.resource.impl.LocalResourceScan;
import org.nutz.resource.impl.WebResourceScan;

/**
 * 考虑作为一个全局的工厂方法,以便在Web环境与非Web环境间切换
 * <p/>想法是这样的,在MVC初始化的时候,就调用这个方法,初始化webResourceScan
 * <p/>非web环境调用此方法,就直接返回localResourceScan
 * <p/>
 * @author wendal
 *
 */
public final class ScanerFatory {

	private static WebResourceScan webResourceScan ;
	
	private static ResourceScan localResourceScan = new LocalResourceScan();
	
	public void init (ServletContext sc) {
		synchronized (webResourceScan) {
			if (sc != null)
				ScanerFatory.webResourceScan = new WebResourceScan(sc);
			else
				ScanerFatory.webResourceScan = null;
		}
	}
	
	public static ResourceScan make() {
		synchronized (webResourceScan) {
			if (webResourceScan != null && webResourceScan.canWork())
				return webResourceScan;
		}
		return localResourceScan;
	}
}
