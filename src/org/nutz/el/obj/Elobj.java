package org.nutz.el.obj;

import org.nutz.el.ElCache;

public interface Elobj {
    public String getVal();
    public Object fetchVal();
    public void setEc(ElCache ec);
}
