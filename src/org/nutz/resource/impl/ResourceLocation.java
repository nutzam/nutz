package org.nutz.resource.impl;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.regex.Pattern;

import org.nutz.resource.NutResource;

public abstract class ResourceLocation {
    
    public abstract String id();

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
    
    public static String getJarPath(String jarPath) {
        if (jarPath.startsWith("zip:"))
            jarPath = jarPath.substring(4);
        if (jarPath.startsWith("file:/")) {
            jarPath = jarPath.substring("file:/".length());
            if (!new File(jarPath).exists() && !jarPath.startsWith("/")) {
                jarPath = "/" + jarPath;
            }
        }
        try {
            return new File(jarPath).getAbsoluteFile().getCanonicalPath();
        }
        catch (IOException e) {
            return jarPath;
        }
    }
    

    
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (obj instanceof ResourceLocation)
            return ((ResourceLocation)obj).id().equals(this.id());
        return false;
    }
    
    public int hashCode() {
        return id().hashCode();
    }
}