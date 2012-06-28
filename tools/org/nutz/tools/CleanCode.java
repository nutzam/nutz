package org.nutz.tools;

import java.io.File;

import org.nutz.lang.Files;
import org.nutz.lang.util.Disks;
import org.nutz.lang.util.FileVisitor;

public class CleanCode {

    public static void main(String[] args) {
        CleanCode.removeTabs("tools");
    }
    
    public static int removeTabs(String path) {
        final int[] re = new int[1];
        Disks.visitFile(new File(path), new FileVisitor() {
            
            public void visit(File file) {
                if (file.isDirectory())
                    return;
                if (!file.getName().endsWith(".java"))
                    return;
                String str = Files.read(file);
                if (!str.contains("\t"))
                    return;
                str = str.replaceAll("\t", "    ");
                Files.write(file, str);
                re[0]++;
            }
        }, null);
        return re[0];
    }
}
