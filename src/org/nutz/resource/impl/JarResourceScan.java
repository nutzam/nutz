package org.nutz.resource.impl;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.nutz.lang.Files;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.resource.NutResource;

/**
 * 在类路径中查找特定的资源
 * 
 * @author wendal(wendal1985@gmail.com)
 * 
 */
public class JarResourceScan extends AbstractResourceScan {

	private static final Log LOG = Logs.getLog(JarResourceScan.class);

	public boolean canWork() {
		return getClassPath() != null;
	}

	/**
	 * 示例:
	 * <p/>
	 * <code>list("org/nutz/",".js")</code>
	 * <p/>
	 * 将返回org/nutz开头, .js结尾的在jar文件中找到的资源
	 * 
	 * @param src
	 *            查找的源
	 * @param filter
	 *            过滤器
	 */
	public List<NutResource> list(final String src, String filter) {
		if (filter == null)
			filter = "";
		List<NutResource> list = new ArrayList<NutResource>(100);
		String[] paths = splitedClassPath();

		if (paths != null) {
			if (LOG.isDebugEnabled())
				LOG.debugf("Scan resource in ClassPath : %s",getClassPath());
			try {
				String pathRegex = src + ".+" + filter;
				for (String path : paths) {
					if (path.endsWith(".jar")) {
						File file = new File(path);
						if (Files.isFile(file)) {
							ZipEntry[] entries = Files.findEntryInZip(new ZipFile(file), pathRegex);
							if (entries != null) {
								for (ZipEntry zipEntry : entries) {
									String entryName = zipEntry.getName();
									try {
										list.add(new ZipEntryResource(path, entryName));
									}
									catch (Throwable e) {
										if (LOG.isDebugEnabled())
											LOG.debug("!!Found a resource but fail to add ,name = "
														+ entryName, e);
									}
								}
							}
						}
					}
				}
			}
			catch (Throwable e) {
				if (LOG.isWarnEnabled())
					LOG.warn("!!Case error when scan resource !", e);
			}
		}
		if (LOG.isInfoEnabled())
			LOG.infof("Found %s resources in src = %s ,filter = %s", list.size(), src, filter);
		return list;
	}

}

class ZipEntryResource extends NutResource {

	private String zipEntryName;

	private String zipFileName;

	public ZipEntryResource(String zipFileName, String zipEntryName) {
		super();
		this.zipFileName = zipFileName;
		this.zipEntryName = zipEntryName;
	}

	@Override
	public InputStream getInputStream() throws IOException {
		ZipFile zipFile = new ZipFile(zipFileName);
		ZipEntry zipEntry = zipFile.getEntry(zipEntryName);
		return zipFile.getInputStream(zipEntry);
	}

	@Override
	public void setName(String name) {
		this.zipEntryName = name;
	}

	@Override
	public String getName() {
		return this.zipEntryName;
	}
}