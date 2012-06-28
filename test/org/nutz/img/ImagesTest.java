package org.nutz.img;

import java.io.File;

import org.junit.Test;
import org.nutz.lang.Files;

public class ImagesTest {

    @Test
    public void test_c() throws Throwable {
        File file = Files.findFile(getClass().getPackage().getName().replace('.', '/')
                                    + "/snapshot.jpg");
        //System.out.println(file.length());
        Images.clipScale(file, File.createTempFile("abc", "jpg"), 256, 256);
    }
    
    @Test
    public void test_clipScale_url() throws Throwable {
        File file = Files.findFile(getClass().getPackage().getName().replace('.', '/')
                                    + "/snapshot.jpg");
        Images.clipScale(file.toURI().toURL(), File.createTempFile("abc", "jpg"), 256, 256);
    }
}
