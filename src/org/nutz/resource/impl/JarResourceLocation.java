package org.nutz.resource.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.nutz.lang.Lang;
import org.nutz.lang.Streams;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.resource.NutResource;
import org.nutz.resource.Scans;

public class JarResourceLocation extends ResourceLocation {
    
    public String id() {
        return jarPath;
    }

    public void scan(String base, Pattern regex, List<NutResource> list) {
        for (String ensName : names) {
                String name = ensName;
                if (name.contains("/"))
                    name = name.substring(name.lastIndexOf('/') + 1);
                if (ensName.startsWith(base) && (null == regex || regex.matcher(name).find())) {
                    try {
                        list.add(Scans.makeJarNutResource(jarPath, ensName, base));
                    } catch (IOException e) {
                        if (log.isInfoEnabled())
                            log.info("Jar delete while scan?!! " + jarPath,e);
                    }
                }
        }
    }
    
    public String toString() {
        return "Jar[path=" + jarPath + "]";
    }
    
    private static final Log log = Logs.get();

    private List<String> names = new ArrayList<String>();

    private String jarPath;

    public JarResourceLocation(String jarPath) throws IOException {
        this.jarPath = ResourceLocation.getJarPath(jarPath);
        ZipInputStream zis = null;
        try {
            zis = Scans.makeZipInputStream(jarPath);
            ZipEntry ens = null;
            while (null != (ens = zis.getNextEntry())) {
                if (ens.isDirectory())
                    continue;
                names.add(ens.getName());
            }
        }
        catch (Throwable e) {
            throw Lang.wrapThrow(e);
        } finally {
            Streams.safeClose(zis);
        }
    }
}
