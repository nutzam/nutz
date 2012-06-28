package org.nutz.el.parse;

import org.nutz.el.ElException;
import org.nutz.el.Parse;

/**
 * 字符串转换器
 * @author juqkai(juqkai@gmail.com)
 *
 */
public class StringParse implements Parse {
    public Object fetchItem(CharQueue exp) {
        //@ JKTODO 添加转意字符
        switch(exp.peek()){
        case '\'':
        case '"':
            StringBuilder sb = new StringBuilder();
            char end = exp.poll();
//            while(!exp.isEmpty() && !exp.peek().equals(end)){
            while(!exp.isEmpty() && exp.peek() != end){
                if(exp.peek() == '\\') {//转义字符?
                    parseSp(exp, sb);
                }else{
                    sb.append(exp.poll());
                }
            }
            exp.poll();
            return sb.toString();
        }
        return nullobj;
    }
    
    private void parseSp(CharQueue exp, StringBuilder sb){
        switch (exp.poll()) {
        case 'n':
            sb.append('\n');
            break;
        case 'r':
            sb.append('\r');
            break;
        case 't':
            sb.append('\t');
            break;
        case '\\':
            sb.append('\\');
            break;
        case '\'':
            sb.append('\'');
            break;
        case '\"':
            sb.append('\"');
            break;
        case 'u':
            char[] hex = new char[4];
            for (int i = 0; i < 4; i++)
                hex[i] = exp.poll();
            sb.append((char)Integer.valueOf(new String(hex), 16).intValue());
            break;
        case 'b': //这个支持一下又何妨?
            sb.append(' ');//空格
            break;
        case 'f':
            sb.append('\f');//这个支持一下又何妨?
            break;
        default:
            throw new ElException("Unexpected char");    //1.b.37及之前的版本,会忽略非法的转义字符
        }
    }

    public static void main(String args[]){
        double x = .1 + 5;
        System.out.println(x);
    }
}
