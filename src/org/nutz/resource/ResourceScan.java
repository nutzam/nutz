package org.nutz.resource;

import java.util.List;

public interface ResourceScan {
	
	List<NutResource> list(String src, String filter);

}
