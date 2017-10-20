package org.nutz.lang.util;

public class LinkedIntArray extends LinkedArray<Integer> {
    
    public LinkedIntArray() {
    }
    
    public LinkedIntArray(int size) {
        super(size);
    }
    
    public int[] toIntArray() {
        int[] array = new int[this.size()];
        Integer[] tmp = this.toArray(new Integer[this.size()]);
        for (int i = 0; i < array.length; i++) {
            array[i] = tmp[i];
        }
        return array;
    }
}
