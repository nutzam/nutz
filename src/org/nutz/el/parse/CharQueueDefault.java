package org.nutz.el.parse;

import java.io.IOException;
import java.io.Reader;
import java.util.LinkedList;

import org.nutz.log.Log;
import org.nutz.log.Logs;

/**
 * 字符队列默认实现.
 * @author juqkai(juqkai@gmail.com)
 *
 */
public class CharQueueDefault implements CharQueue{
    private static final Log log = Logs.get();
    private Reader reader;
    private LinkedList<Integer> cache;
    private int cursor;
    
    public CharQueueDefault(Reader reader) {
        this.reader = reader;
        cache = new LinkedList<Integer>();
        try {
            cursor = reader.read();
        } catch (IOException e) {
            log.debug("read error", e);
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
                log.debug("read error", e);
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
            log.debug("read error", e);
        }
        return x;
    }

    public boolean isEmpty() {
        return cursor == -1;
    }

}
