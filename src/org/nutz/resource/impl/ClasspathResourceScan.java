package org.nutz.resource.impl;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.resource.NutResource;
import org.nutz.resource.ResourceScan;

public class ClasspathResourceScan extends AbstractResourceScan {
	
	private static final Log LOG = Logs.getLog(ClasspathResourceScan.class);
	
	private static ResourceScan filesystemResourceScan = new FilesystemResourceScan();
	
	public boolean canWork() {
		return getClassPath() != null;
	}

	public List<NutResource> list(String src, String filter) {
		if (filter == null)
			filter = "";
		if (!src.startsWith("/"))
			src = "/" + src;
		List<NutResource> list = new ArrayList<NutResource>(100);
		String[] paths = splitedClassPath();

		if (paths != null) {
			if (LOG.isDebugEnabled())
				LOG.debugf("Scan resource in : %s",getClassPath());
			for (String path : paths) {
				try {
					if (path.endsWith("/"))
						path = path.substring(0,path.length() - 1);
					File file = new File(path+src);
					if (file.exists()) {
						if (file.isFile() && src.endsWith(filter))
							list.add(new ClasspathResource(file.toURI().toURL(),src.substring(1)));
						else if (file.isDirectory()) {
							list.addAll(filesystemResourceScan.list(file.getPath(), filter));
						}
					}
				}
				catch (Throwable e) {
					if (LOG.isWarnEnabled())
						LOG.warn("!!Case error when scan resource !", e);
				}
			}
		}
		if (LOG.isInfoEnabled())
			LOG.infof("Found %s resources in src = %s ,filter = %s", list.size(), src, filter);
		return list;
	}


}

class ClasspathResource extends NutResource {

	private URL url;

	public ClasspathResource(URL url, String name) {
		this.url = url;
		this.name = name;
	}

	@Override
	public InputStream getInputStream() throws IOException {
		return url.openStream();
	}

}
