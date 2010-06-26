package org.nutz.resource;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;
import org.nutz.resource.impl.ClasspathResourceScanTest;
import org.nutz.resource.impl.FilesystemResourceScanTest;
import org.nutz.resource.impl.JarResourceScanTest;

@RunWith(Suite.class)
@SuiteClasses({FilesystemResourceScanTest.class,
              JarResourceScanTest.class,
              ClasspathResourceScanTest.class})
public class AllResourceScan {

}
