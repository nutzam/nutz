package org.nutz.resource.impl;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.nutz.NutRuntimeException;
import org.nutz.lang.Lang;
import org.nutz.lang.Streams;
import org.nutz.lang.util.Disks;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.resource.NutResource;
import org.nutz.resource.Scans;

public abstract class ResourceLocation {

    public abstract void scan(String base, Pattern pattern, List<NutResource> list);
    
    public static ResourceLocation file(File root) {
        try {
            if (!root.exists())
                return ErrorResourceLocation.make(root);
            return new FileSystemResourceLocation(root.getAbsoluteFile().getCanonicalFile());
        } catch (Exception e) {
            return ErrorResourceLocation.make(root);
        }
    }
    
    public static ResourceLocation jar(String jarPath) {
        try {
            return new JarResourceLocation(jarPath);
        } catch (Exception e) {
            return ErrorResourceLocation.make(jarPath);
        }
    }
}

class FileSystemResourceLocation extends ResourceLocation {
    
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (!(obj instanceof FileSystemResourceLocation))
            return false;
        return root.equals(((FileSystemResourceLocation) obj).root);
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

    public FileSystemResourceLocation(File root) throws IOException {
        if (root == null)
            throw new NutRuntimeException("FileSystemResourceLocation root can't be NULL");
        this.root = root.getAbsoluteFile().getCanonicalFile();
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

    public JarResourceLocation(String jarPath) throws IOException {
        if (jarPath.startsWith("zip:"))
            jarPath = jarPath.substring(4);
        if (jarPath.startsWith("file:/")) {
            jarPath = jarPath.substring("file:/".length());
            if (!new File(jarPath).exists() && !jarPath.startsWith("/")) {
                jarPath = "/" + jarPath;
            }
        }
        this.jarPath = new File(jarPath).getAbsoluteFile().getCanonicalPath();
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
