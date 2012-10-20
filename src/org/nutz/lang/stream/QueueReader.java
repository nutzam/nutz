package org.nutz.lang.stream;

import java.io.IOException;
import java.io.Reader;
import java.util.LinkedList;

/**
 * 队列InputStream
 * @author juqkai(juqkai@gmail.com)
 */
public class QueueReader extends Reader{
    //原始流
    private Reader is;
    //缓存
    private LinkedList<Integer> cache = new LinkedList<Integer>();
    //peek索引
    private int peekindex = 0;
    //是否到流尾
    private boolean end = false;
    //列
    private int col = 0;
    //行
    private int row = 1;
    
    public QueueReader(Reader is) {
        this.is = is;
    }
    
    /**
     * 读取一项数据
     * @param ends 结束符, 默认' ', '\r', '\n'
     * @return
     * @throws IOException
     */
    public String readItem(char ... ends) throws IOException{
        StringBuilder sb = new StringBuilder();
        while(true){
            switch (peek()) {
            case ' ':
            case '\r':
            case '\n':
            case -1:
                return sb.toString();
            default:
                for(Character c : ends){
                    if(c.equals(peek())){
                        return sb.toString();
                    }
                }
                sb.append((char)poll());
            }
        }
    }
    
    /**
     * 读取一行
     * @return
     * @throws IOException
     */
    public String readLine() throws IOException{
        StringBuilder sb = new StringBuilder();
        for(;;){
            int v = peek();
            if(v == '\r' || v == '\n'){
                poll();
                v = peekNext();
                if(v == '\r' || v == '\n'){
                    poll();
                }
                break;
            }
            sb.append((char)poll());
        }
        return sb.toString();
    }
    
    /**
     * 读取头部字节, 并删除
     * @return
     * @throws IOException
     */
    public int poll() throws IOException{
        peekindex = 0;
        int v = -1;
        if(cache.size() <= 0){
            v = is.read();
        } else {
            v = cache.poll();
        }
        if(v == -1){
            end = true;
        }
        if(v == '\n'){
            col = 0;
            row ++;
        } else {
            col ++;
        }
        return v;
    }
    
    /**
     * 访问头部开始第几个字节, 不删除
     * @param index
     * @return
     * @throws IOException 
     */
    public int peek(int index) throws IOException{
        while(cache.size() <= index){
            cache.add(is.read());
        }
        return cache.get(index);
    }
    
    /**
     * 访问上次peekNext访问的下个位置的字节, 未访问过则访问索引0, poll, peek后归零, 不删除
     * @return
     * @throws IOException 
     */
    public int peekNext() throws IOException{
        return peek(peekindex ++);
    }
    
    /**
     * 访问头部字节, 不删除
     * @return
     * @throws IOException 
     */
    public int peek() throws IOException{
        peekindex = 0;
        int v = peek(peekindex ++);
        if(v == -1){
            end = true;
        }
        return v;
    }
    
    /**
     * 跳过和丢弃输入流中的数据
     */
    public long skip(long n) throws IOException {
        int s = cache.size();
        if(s > 0){
            if(s < n){
                n = n - s;
            } else {
                for(int i = 0; i < n; i ++){
                    cache.poll();
                }
                return n;
            }
        }
        return super.skip(n) + s;
    }
    
    /**
     * 是否结束
     * @return
     */
    public boolean isEnd(){
        return end;
    }
    
    public int read(char[] cbuf, int off, int len) throws IOException {
        for(int i = 0; i < len ; i++){
            if(isEnd()){
                return -1;
            }
            cbuf[off + i] = (char) poll();
        }
        return len;
    }
    
    public void close() throws IOException {
        is.close();
        cache.clear();
    }
    
    /**
     * 是否以 start 开始
     * @param start
     * @return
     * @throws IOException
     */
    public boolean startWith(String start) throws IOException{
        char[] cs = start.toCharArray();
        int i = 0;
        for(;i < cs.length; i ++){
            if(peek(i) != cs[i]){
                return false;
            }
        }
        return true;
    }

    public int getCol() {
        return col;
    }

    public int getRow() {
        return row;
    }
}
