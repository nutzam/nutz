package org.nutz.resource.impl;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.nutz.lang.Files;
import org.nutz.resource.NutResource;
import org.nutz.resource.ResourceScan;

public class LocalResourceScanTest {

	private ResourceScan scan;

	@Before
	public void before() {
		scan = new LocalResourceScan();
	}

	@Test
	public void test_in_normal_file() throws IOException {
		String testPath = "~/nutz/unit/rs/test";
		File testDir = Files.createDirIfNoExists(testPath);
		Files.clearDir(testDir);
		List<NutResource> list = scan.list(testPath, ".*");
		assertEquals(0, list.size());

		Files.createDirIfNoExists(testPath + "/a/b/c");
		list = scan.list(testPath, ".*");
		assertEquals(0, list.size());

		Files.createFileIfNoExists(testPath + "/a/b/c/l.txt");
		Files.createFileIfNoExists(testPath + "/a/b/c/m.doc");
		Files.createFileIfNoExists(testPath + "/a/b/c/n.jpg");
		Files.createFileIfNoExists(testPath + "/a/b/c/o.jpg");
		list = scan.list(testPath, ".*");
		assertEquals(4, list.size());

		list = scan.list(testPath, ".+[.]jpg");
		assertEquals(2, list.size());

		list = scan.list(testPath, ".*[.]txt");
		assertEquals(1, list.size());

		Files.deleteDir(testDir);
	}

	@Test
	public void test_in_classpath() {
		String testPath = ResourceScan.class.getName().replace('.', '/') + ".class";
		String testFilter = "^" + ResourceScan.class.getSimpleName() + ".class$";
		List<NutResource> list = scan.list(testPath, testFilter);
		assertEquals(1, list.size());
	}
	
	@Test
	public void test_in_jar(){
		String testPath = String.class.getName().replace('.', '/') + ".class";
		String testFilter = "^.*(String|System)[.]class$";
		List<NutResource> list = scan.list(testPath, testFilter);
		assertEquals(2, list.size());
	}
}
