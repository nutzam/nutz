package org.nutz.resource.impl;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import javax.servlet.ServletContext;

import org.nutz.resource.NutResource;

public class WebClassesResourceLocation extends ResourceLocation {

    protected ServletContext sc;
    
    public int priority = 125;

    public WebClassesResourceLocation(ServletContext sc) {
        this.sc = sc;
    }

    public String id() {
        return "/WEB-INF/classes/";
    }

    @Override
    public void scan(String base, Pattern pattern, List<NutResource> list) {
        if (!base.startsWith("/"))
            base = "/" + base;
        List<String> paths = new ArrayList<String>();
        getResources("/WEB-INF/classes"+base, paths);
        for (final String path : paths) {
            if (path.equals(base)) {
                list.add(new WebClassesResource(path, base));
                continue;
            }
            String name = path.substring(path.lastIndexOf('/'));
            if (pattern == null || pattern.matcher(name).find()) {
                list.add(new WebClassesResource(path, base));
            }
        }
    }
    
    public class WebClassesResource extends NutResource {
        protected String path;
        protected String base;
        public WebClassesResource(String path, String base) {
            setSource("webapp:"+path);
            this.path = path;
            this.base = base;
            if (path.equals("/WEB-INF/classes"+base))
                setName(path);
            else
                setName(path.substring(("/WEB-INF/classes"+base).length()));
            setPriority(priority);
        }
        public InputStream getInputStream() throws IOException {
            return sc.getResourceAsStream(path);
        }
        
        public String toString() {
            return "webapp:" + path;
        }
    }
    
    public void getResources(String base, List<String> list) {
        Set<String> paths = sc.getResourcePaths(base);
        if (paths == null)
            return;
        for (final String path : paths) {
            if (path.endsWith("/"))
                getResources(path, list);
            else
                list.add(path);
        }
    }
}
