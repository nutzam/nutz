package org.nutz.el.parse;

import org.nutz.el.ElException;
import org.nutz.el.Parse;

/**
 * 数值转换器
 * @author juqkai(juqkai@gmail.com)
 *
 */
public class ValParse implements Parse {

    public Object fetchItem(CharQueue exp){
        StringBuilder sb = new StringBuilder();
        switch(exp.peek()){
        case '.':
        case '0':
        case '1':
        case '2':
        case '3':
        case '4':
        case '5':
        case '6':
        case '7':
        case '8':
        case '9':
            boolean hasPoint = exp.peek() == '.';
            sb.append(exp.poll());
            while(!exp.isEmpty()){
                switch(exp.peek()){
                case '0':
                case '1':
                case '2':
                case '3':
                case '4':
                case '5':
                case '6':
                case '7':
                case '8':
                case '9':
                    sb.append(exp.poll());
                    break;
                case '.':
                    if(hasPoint){
                        throw new ElException("表达式错误,请查看是否有多个'.'!");
                    }
                    hasPoint = true;
                    sb.append(exp.poll());
                    break;
                case 'l':
                case 'L':
                    sb.append(exp.poll());
                    return Long.parseLong(sb.toString());
                case 'f':
                case 'F':
                    sb.append(exp.poll());
                    return Float.parseFloat(sb.toString());
                case 'd':
                case 'D':
                    sb.append(exp.poll());
                    return Double.parseDouble(sb.toString());
                default:
                    if(hasPoint){
                        return Double.parseDouble(sb.toString());
                    }
                    return Integer.parseInt(sb.toString());
                }
            }
            if(hasPoint){
                return Double.parseDouble(sb.toString());
            }
            return Integer.parseInt(sb.toString());
        }
        return nullobj;
    }
    
}
