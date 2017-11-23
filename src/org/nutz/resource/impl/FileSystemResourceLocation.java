package org.nutz.resource.impl;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.regex.Pattern;

import org.nutz.lang.util.Disks;
import org.nutz.resource.NutResource;
import org.nutz.resource.Scans;

public class FileSystemResourceLocation extends ResourceLocation {
    
    public int priority = 150;

    public String id() {
        return root.getAbsolutePath();
    }
    
    public void scan(final String base, final Pattern pattern, final List<NutResource> list) {
        final File baseFile = new File(root.getAbsolutePath()+"/"+base);
        if (baseFile.isFile()) {
            list.add(new FileResource(baseFile).setPriority(priority));
            return;
        }
        
        Disks.visitFile(baseFile, new Scans.ResourceFileVisitor(list, base, priority), new Scans.ResourceFileFilter(pattern));
    }

    public String toString() {
        return "Dir[path=" + root + "]";
    }

    private File root;

    public FileSystemResourceLocation(File root) throws IOException {
        if (root == null)
            throw new RuntimeException("FileSystemResourceLocation root can't be NULL");
        this.root = root.getAbsoluteFile().getCanonicalFile();
    }
}