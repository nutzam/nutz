package org.nutz.resource.impl;

import java.io.File;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import org.nutz.lang.Files;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.resource.NutResource;

/**
 * 在文件系统中查找资源
 * 
 * @author wendal(wendal1985@gmail.com)
 * 
 */
public class FilesystemResourceScan extends AbstractResourceScan {

	private static final Log LOG = Logs.getLog(FilesystemResourceScan.class);

	public boolean canWork() {
		return !System.getProperties().containsKey("com.google.appengine.runtime.version");
	}

	/**
	 * 示例:
	 * <p/>
	 * <code>list("org/nutz/",".js")</code>
	 * <p/>
	 * 将返回org/nutz文件夹及子文件夹下全部js文件
	 * 
	 * @param src
	 *            查找的源
	 * @param filter
	 *            过滤器
	 */
	public List<NutResource> list(String src, String filter) {
		if (filter == null)
			filter = "";
		List<NutResource> list = new ArrayList<NutResource>(100);
		File srcFile = Files.findFile(src);
		if (srcFile != null) {
			if (!srcFile.isDirectory())
				srcFile = srcFile.getParentFile();
			File[] dirs = Files.scanDirs(srcFile);
			for (File dir : dirs) {
				File[] files = Files.files(dir, filter);
				for (File file : files) {
					NutResource nutResource = new NutResource();
					try {
						nutResource.setUrl(file.toURI().toURL());
						list.add(nutResource);
					}
					catch (MalformedURLException e) {
						if (LOG.isDebugEnabled())
							LOG.debug("!!Found a resource but fail to add ,file = " + file, e);
					}
				}
			}
		}
		if (LOG.isInfoEnabled())
			LOG.infof("Found %s resources in src = %s ,filter = %s", list.size(), src, filter);
		return list;
	}

}
