package org.nutz.el.parse;

import org.nutz.el.Parse;
import org.nutz.el.obj.AbstractObj;
import org.nutz.el.obj.IdentifierObj;

/**
 * 标识符转换
 * @author juqkai(juqkai@gmail.com)
 *
 */
public class IdentifierParse implements Parse{

    public Object fetchItem(CharQueue exp) {
        StringBuilder sb = new StringBuilder();
        if(Character.isJavaIdentifierStart(exp.peek())){
            sb.append(exp.poll());
            while(!exp.isEmpty() && Character.isJavaIdentifierPart(exp.peek())){
                sb.append(exp.poll());
            }
            if(sb.toString().equals("null")){
                return new IdentifierObj(null);
            }
            if(sb.toString().equals("true")){
                return Boolean.TRUE;
            }
            if(sb.toString().equals("false")){
                return Boolean.FALSE;
            }
            return new AbstractObj(sb.toString());
        }
        return nullobj;
    }

}
