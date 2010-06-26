package org.nutz.resource.impl;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
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
		return null == System.getProperties().get("com.google.appengine.runtime.version");
	}

	/**
	 * 示例:
	 * <p/>
	 * <code>new FilesystemResourceScan().list("org/nutz/",".js")</code>
	 * <p/>
	 * 将返回org/nutz文件夹及子文件夹下全部js文件
	 * 
	 * @param src
	 *            查找的源
	 * @param filter
	 *            过滤器
	 */
	public List<NutResource> list(String src, String filter) {
		List<NutResource> list = new ArrayList<NutResource>();
		if (src != null) {
			if (filter == null)
				filter = "";
			File srcFile = new File(src);
			if (!srcFile.isDirectory())
				srcFile = srcFile.getParentFile();
			File[] dirs = Files.scanDirs(srcFile);
			for (File dir : dirs) {
				File[] files = Files.files(dir, filter);
				if (files == null)
					continue;
				for (File file : files) {
					try {
						NutResource nutResource = new FileResource(file.toURI().toURL(),file.getPath());
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

class FileResource extends NutResource {

	private URL url;

	public FileResource(URL url, String name) {
		this.url = url;
		this.name = name;
	}

	@Override
	public InputStream getInputStream() throws IOException {
		return url.openStream();
	}

}
