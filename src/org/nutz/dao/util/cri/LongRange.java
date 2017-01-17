package org.nutz.dao.util.cri;

public class LongRange extends NumberRange {

    private static final long serialVersionUID = 1L;

    LongRange(String name, long... ids) {
        super(name);
        this.ids = ids;
        this.not = false;
    }

}
