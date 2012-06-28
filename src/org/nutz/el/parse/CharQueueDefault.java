package org.nutz.el.parse;

import java.io.IOException;
import java.io.Reader;
import java.util.LinkedList;

/**
 * 字符队列默认实现.
 * @author juqkai(juqkai@gmail.com)
 *
 */
public class CharQueueDefault implements CharQueue{
    private Reader reader;
    private LinkedList<Integer> cache;
    private int cursor;
    
    public CharQueueDefault(Reader reader) {
        this.reader = reader;
        cache = new LinkedList<Integer>();
        try {
            cursor = reader.read();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public char peek() {
        return (char) cursor;
    }
    
    public char peek(int ofset){
        if(ofset == 0){
            return (char) cursor;
        }
        //这个地方因为已经预读了cursor 所以,偏移量要向后移动一位
        if(cache.size() > ofset - 1){
            return (char)cache.get(ofset - 1).intValue();
        }
        int t = 0;
        for(int i = 0; i < ofset - cache.size(); i++){
            try {
                t = reader.read();
                cache.add(t);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return (char) t;
    }

    public char poll() {
        char x = (char) cursor;
        try {
            if(cache.isEmpty()){
                cursor = reader.read();
            } else {
                cursor = cache.poll();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return x;
    }

    public boolean isEmpty() {
        return cursor == -1;
    }

}
