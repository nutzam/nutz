package org.nutz.resource.impl;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.nutz.lang.Lang;
import org.nutz.lang.util.Disks;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.resource.NutResource;
import org.nutz.resource.Scans;

public abstract class ResourceLocation {

    public abstract void scan(String base, Pattern pattern, List<NutResource> list);
    
    public static ResourceLocation file(File root) {
        if (!root.exists())
            return new ErrorResourceLocation(root);
        try {
            return new FileSystemResourceLocation(root.getAbsoluteFile().getCanonicalFile());
        } catch (Exception e) {
            return new ErrorResourceLocation(root);
        }
    }
    
    public static ResourceLocation jar(String jarPath) {
        try {
            return new JarResourceLocation(jarPath);
        } catch (Exception e) {
            return new ErrorResourceLocation(jarPath);
        }
    }
}

class FileSystemResourceLocation extends ResourceLocation {
    
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        FileSystemResourceLocation other = (FileSystemResourceLocation) obj;
        if (root == null) {
            if (other.root != null)
                return false;
        } else if (!root.equals(other.root))
            return false;
        return true;
    }
    
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((root == null) ? 0 : root.hashCode());
        return result;
    }
    
    public void scan(final String base, final Pattern pattern, final List<NutResource> list) {
        final File baseFile = new File(root.getAbsolutePath()+"/"+base);
        if (baseFile.isFile()) {
            list.add(new FileResource(baseFile));
            return;
        }
        
        Disks.visitFile(baseFile, new Scans.ResourceFileVisitor(list, base), new Scans.ResourceFileFilter(pattern));
    }

    public String toString() {
        return "FileSystemResourceLocation [root=" + root + "]";
    }

    File root;

    public FileSystemResourceLocation(File root) {
        this.root = root;
    }
}

class JarResourceLocation extends ResourceLocation {
    
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        JarResourceLocation other = (JarResourceLocation) obj;
        if (jarPath == null) {
            if (other.jarPath != null)
                return false;
        } else if (!jarPath.equals(other.jarPath))
            return false;
        return true;
    }
    
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((jarPath == null) ? 0 : jarPath.hashCode());
        return result;
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
        return "JarResourceLocation [jarPath=" + jarPath + "]";
    }
    
    private static final Log log = Logs.get();

    private List<String> names = new ArrayList<String>();

    String jarPath;

    public JarResourceLocation(String jarPath) {
        this.jarPath = jarPath;
        try {
            ZipInputStream zis = Scans.makeZipInputStream(jarPath);
            ZipEntry ens = null;
            while (null != (ens = zis.getNextEntry())) {
                if (ens.isDirectory())
                    continue;
                names.add(ens.getName());
            }
            zis.close();
        }
        catch (Exception e) {
            throw Lang.wrapThrow(e);
        }
    }
}
