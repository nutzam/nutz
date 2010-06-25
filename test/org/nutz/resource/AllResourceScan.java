package org.nutz.resource;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;
import org.nutz.resource.impl.FilesystemResourceScanTest;
import org.nutz.resource.impl.JarResourceScanTest;

@RunWith(Suite.class)
@SuiteClasses({FilesystemResourceScanTest.class,
              JarResourceScanTest.class})
public class AllResourceScan {

}
