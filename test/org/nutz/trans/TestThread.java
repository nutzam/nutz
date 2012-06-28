package org.nutz.trans;

import java.util.ArrayList;
import java.util.List;

import org.nutz.lang.Each;
import org.nutz.lang.ExitLoop;
import org.nutz.lang.Lang;

import junit.framework.TestCase;

public abstract class TestThread extends Thread {

    public TestThread() {
        results = new ArrayList<Boolean>();
    }

    private List<Boolean> results;

    protected abstract void doTest();

    @Override
    public void run() {
        doTest();
        synchronized (this) {
            try {
                this.wait(1000);
            }
            catch (InterruptedException e) {
                throw Lang.wrapThrow(e);
            }
        }
    }

    protected void addResult(boolean b) {
        results.add(b);
    }

    public void doAssert() {
        Lang.each(results, new Each<Boolean>() {
            public void invoke(int index, Boolean b, int size) throws ExitLoop {
                System.out.println(index);
                TestCase.assertTrue(b);
            }
        });
    }
}
