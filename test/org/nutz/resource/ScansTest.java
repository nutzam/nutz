package org.nutz.resource;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.servlet.Servlet;

import junit.extensions.ActiveTestSuite;
import junit.extensions.RepeatedTest;
import junit.extensions.TestSetup;
import junit.framework.Assert;

import org.junit.Test;
import org.nutz.lang.Files;
import org.nutz.lang.Strings;

public class ScansTest {

    @Test
    public void test_loadResource() throws IOException {
        String RNAME = "junit/runner/Version.class";

        List<NutResource> nrs = Scans.me().loadResource(".*.class", RNAME);
        assertEquals(1, nrs.size());
        NutResource nr = nrs.get(0);
        assertTrue(nr.getName().indexOf(RNAME) >= 0);
        InputStream ins = nr.getInputStream();
        int len = 0;
        while (-1 != ins.read()) {
            len++;
        }
        ins.close();
        assertTrue(len > 600);
    }

    @Test
    public void test_in_normal_file() throws IOException {
        String testPath = "~/nutz/unit/rs/test";
        File testDir = Files.createDirIfNoExists(testPath);
        Files.clearDir(testDir);
        List<NutResource> list = Scans.me().scan(testPath, ".*");
        assertEquals(0, list.size());

        Files.createDirIfNoExists(testPath + "/a/b/c");
        list = Scans.me().scan(testPath, ".*");
        assertEquals(0, list.size());

        Files.createFileIfNoExists(testPath + "/a/b/c/l.txt");
        Files.createFileIfNoExists(testPath + "/a/b/c/m.doc");
        Files.createFileIfNoExists(testPath + "/a/b/c/n.jpg");
        Files.createFileIfNoExists(testPath + "/a/b/c/o.jpg");
        list = Scans.me().scan(testPath, ".*");
        assertEquals(4, list.size());

        list = Scans.me().scan(testPath, null);
        assertEquals(4, list.size());

        list = Scans.me().scan(testPath, ".+[.]jpg");
        assertEquals(2, list.size());

        list = Scans.me().scan(testPath, ".*[.]txt");
        assertEquals(1, list.size());

        Files.deleteDir(testDir);
    }

    @Test
    public void test_in_classpath() {
        String testPath = Scans.class.getName().replace('.', '/') + ".class";
        String testFilter = "^" + Scans.class.getSimpleName() + ".class$";
        List<NutResource> list = Scans.me().scan(testPath, testFilter);
        assertEquals(1, list.size());
    }

    // @Ignore
    @Test
    public void test_in_jar() {
        String testPath = Assert.class.getPackage().getName().replace('.', '/');
        String testFilter = "^.*(Assert|Test)\\.class$";
        List<NutResource> list = Scans.me().scan(testPath, testFilter);
        //Collections.sort(list);
        assertEquals(2, list.size());
        assertTrue(list.get(0).getName().endsWith("Assert.class"));
        assertTrue(list.get(1).getName().endsWith("Test.class"));
    }

    // @Ignore
    @Test
    public void test_classes_in_jar() {
        List<Class<?>> list = Scans.me()
                                    .scanPackage(    ActiveTestSuite.class,
                                                    ".*(ActiveTestSuite|RepeatedTest|TestSetup)\\.class$");
        assertEquals(3, list.size());
        Collections.sort(list, new Comparator<Class<?>>() {
            public int compare(Class<?> o1, Class<?> o2) {
                return o1.getSimpleName().compareTo(o2.getSimpleName());
            }
        });
        assertTrue(ActiveTestSuite.class == list.get(0));
        assertTrue(RepeatedTest.class == list.get(1));
        assertTrue(TestSetup.class == list.get(2));
    }

    @Test
    public void test_classes_in_package_path() {
        List<Class<?>> list = Scans.me().scanPackage("org.nutz", "Strings.class");
        assertEquals(1, list.size());
        assertTrue(Strings.class == list.get(0));
    }

    @Test
    public void test_scan_with_unexists_file() {
        List<NutResource> list = Scans.me().scan("org/nutz/lang/notExist.class", null);
        assertEquals(0, list.size());
    }

    @Test
    public void test_class_in_jar() {
        String testPath = Test.class.getName().replace('.', '/') + ".class";
        String testFilter = "^Test.class$";
        List<NutResource> list = Scans.me().scan(testPath, testFilter);
        assertEquals(1, list.size());
    }
    
    @Test
    public void test_path_in_jar() {
        String testPath = Test.class.getPackage().getName().replace('.', '/');
        List<NutResource> list = Scans.me().scan(testPath, null);
        assertTrue(list.size() > 10);
    }
    
    @Test
    public void test_scan_root() {
        Scans.me().scan("", ".+\\.xml");
    }
    
    @Test
    public void test_resource_jar() throws MalformedURLException {
    	Scans.me().registerLocation(Servlet.class);
    	Scans.me().registerLocation(getClass().getClassLoader().getResource("javax/servlet/Servlet.class"));
    }
}
