package org.nutz.tools;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;

import org.nutz.lang.Files;
import org.nutz.lang.Streams;
import org.nutz.lang.util.Disks;
import org.nutz.lang.util.FileVisitor;

public class UTF8_BOM {

    public static void main(String[] args) {
        final byte[] UTF_BOM = new byte[]{(byte) 0xEF, (byte) 0xBB, (byte) 0xBF};
        final byte[] bom = new byte[3];
        Disks.visitFile(new File("."), new FileVisitor() {
            
            public void visit(File file) {
                try {
                    FileInputStream fis = new FileInputStream(file);
                    fis.read(bom);
                    if (bom[0] == UTF_BOM[0] && bom[1] == UTF_BOM[1] && bom[2] == UTF_BOM[2]) {
                        System.out.println("Found BOM --> " + file);
                        byte[] data = Streams.readBytes(fis);
                        fis.close();
                        Files.write(file, data);
                        System.out.println("Fixed");
                    }
                }
                catch (Throwable e) {
                    e.printStackTrace();
                }
            }
        }, new FileFilter() {
            
            public boolean accept(File pathname) {
                if (pathname.isDirectory())
                    return true;
                return pathname.getName().endsWith(".java") && pathname.length() > 3;
            }
        });
    }
}
