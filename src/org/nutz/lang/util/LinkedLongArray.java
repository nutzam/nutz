package org.nutz.lang.util;

public class LinkedLongArray extends LinkedArray<Long> {
    
    public LinkedLongArray() {
    }

    public LinkedLongArray(int size) {
        super(size);
    }
    
    public long[] toLongArray() {
        long[] array = new long[this.size()];
        Long[] tmp = this.toArray(new Long[this.size()]);
        for (int i = 0; i < array.length; i++) {
            array[i] = tmp[i];
        }
        return array;
    }
    
}
