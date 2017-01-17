package org.nutz.dao.util.cri;

public class IntRange extends NumberRange {

    private static final long serialVersionUID = 1L;

    IntRange(String name, int... ids) {
        super(name);
        this.not = false;
        this.ids = new long[ids.length];
        for (int i = 0; i < ids.length; i++)
            this.ids[i] = ids[i];
    }
    
}
