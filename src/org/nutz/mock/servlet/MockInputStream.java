package org.nutz.mock.servlet;

import javax.servlet.ServletInputStream;

public abstract class MockInputStream extends ServletInputStream {

    public abstract void init();

    public abstract void append(String name, String value);

}
