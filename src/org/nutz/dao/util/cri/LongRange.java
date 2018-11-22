package org.nutz.dao.util.cri;

public class LongRange extends NumberRange {

    private static final long serialVersionUID = 1L;

    LongRange(String name, long... ids) {
        super(name);
        this.ids = ids;
        this.not = false;
    }
    
    LongRange(String name, Long[] ids) {
    	super(name);
    	this.ids =  new long[ids.length];
    	for (int i = 0; i < ids.length; i++)
    		this.ids[i] = ids[i];
    	this.not = false;
    }

}
