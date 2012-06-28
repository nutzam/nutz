package org.nutz.trans;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({    SimpleTransTest.class,
                        TransactionTest.class,
                        BatchTransTest.class,
                        TransLevelTest.class})
public class AllTrans {}
