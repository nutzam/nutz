package org.nutz.resource;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.junit.Test;
import org.nutz.lang.Files;
import org.nutz.lang.Strings;

public class ScansTest {

	@Test
	public void test_in_normal_file() throws IOException {
		String testPath = "~/nutz/unit/rs/test";
		File testDir = Files.createDirIfNoExists(testPath);
		Files.clearDir(testDir);
		List<NutResource> list = Scans.inLocal(testPath, ".*");
		assertEquals(0, list.size());

		Files.createDirIfNoExists(testPath + "/a/b/c");
		list = Scans.inLocal(testPath, ".*");
		assertEquals(0, list.size());

		Files.createFileIfNoExists(testPath + "/a/b/c/l.txt");
		Files.createFileIfNoExists(testPath + "/a/b/c/m.doc");
		Files.createFileIfNoExists(testPath + "/a/b/c/n.jpg");
		Files.createFileIfNoExists(testPath + "/a/b/c/o.jpg");
		list = Scans.inLocal(testPath, ".*");
		assertEquals(4, list.size());

		list = Scans.inLocal(testPath, null);
		assertEquals(4, list.size());

		list = Scans.inLocal(testPath, ".+[.]jpg");
		assertEquals(2, list.size());

		list = Scans.inLocal(testPath, ".*[.]txt");
		assertEquals(1, list.size());

		Files.deleteDir(testDir);
	}

	@Test
	public void test_in_classpath() {
		String testPath = ResourceScan.class.getName().replace('.', '/') + ".class";
		String testFilter = "^" + ResourceScan.class.getSimpleName() + ".class$";
		List<NutResource> list = Scans.inLocal(testPath, testFilter);
		assertEquals(1, list.size());
	}

	@Test
	public void test_in_jar() {
		String testPath = String.class.getName().replace('.', '/') + ".class";
		String testFilter = "^.*(String|System)[.]class$";
		List<NutResource> list = Scans.inLocal(testPath, testFilter);
		Collections.sort(list);
		assertEquals(2, list.size());
		assertTrue(list.get(0).getName().endsWith("String.class"));
		assertTrue(list.get(1).getName().endsWith("System.class"));
	}

	@Test
	public void test_classes_in_jar() {
		List<Class<?>> list = Scans.inLocal(String.class, "^(Object|Array|Number)([.]class)$");
		assertEquals(3, list.size());
		Collections.sort(list, new Comparator<Class<?>>() {
			public int compare(Class<?> o1, Class<?> o2) {
				return o1.getSimpleName().compareTo(o2.getSimpleName());
			}
		});
		assertTrue(Array.class == list.get(0));
		assertTrue(Number.class == list.get(1));
		assertTrue(Object.class == list.get(2));
	}

	@Test
	public void test_classes_in_package_path() {
		List<Class<?>> list = Scans.inLocalPackage("org.nutz", "Strings.class");
		assertEquals(1, list.size());
		assertTrue(Strings.class == list.get(0));
	}

	@Test
	public void test_scan_with_unexists_file() {
		List<NutResource> list = Scans.inLocal("org/nutz/lang/notExist.class", null);
		assertEquals(0, list.size());
	}

}
