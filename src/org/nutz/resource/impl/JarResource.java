package org.nutz.resource.impl;

import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.nutz.lang.Lang;
import org.nutz.resource.NutResource;
import org.nutz.resource.Scans;

public class JarResource extends NutResource {

    protected String jarPath;
    protected String entryName;
    
    public JarResource(String jarPath, String entryName) {
        super();
        this.jarPath = jarPath;
        this.entryName = entryName;
        priority = 50;
    }
    
    @Override
    public InputStream getInputStream() throws IOException {
        ZipInputStream zis = Scans.makeZipInputStream(jarPath);
        ZipEntry ens = null;
        while (null != (ens = zis.getNextEntry())) {
            if (ens.getName().equals(entryName)) {
                return zis;
            }
        }
        throw Lang.impossible();
    }
    
    @Override
    public int hashCode() {
        return (jarPath + ":" + entryName).hashCode();
    }
    
    @Override
    public String toString() {
        return String.format("Jar[%s:%s]", jarPath, entryName);
    }
}
