package org.nutz.el.opt.logic;

import org.nutz.el.ElException;
import org.nutz.el.opt.TwoTernary;

/**
 * 三元运算符:
 * ':'
 * <br>
 * 说明,三元表达式包含两个运算符:'?',':'.整个表达式的结果由它们共同完成.而每个符号承担一部分操作.<br>
 * <li>'?':包含两个操作对象,即,'?'左侧的逻辑表达式,与'?'右侧的第一值.<br>
 * <li>':':也包含两个操作对象,即,':'前面生成的'?'对象,与':'右侧的第二个值.<br>
 * 在进行运算的时候,是先运算':',而':'中将条件的判断委托到'?'当中.然后':'对象根据'?'中的返回
 * 结果分别读取'?'中的的左值或,':'的右值
 * @author juqkai(juqkai@gmail.com)
 *
 */
public class QuestionSelectOpt extends TwoTernary{
    public int fetchPriority() {
        return 13;
    }
    public Object calculate() {
        if(!(left instanceof QuestionOpt)){
            throw new ElException("三元表达式错误!");
        }
        QuestionOpt qo = (QuestionOpt) left;
        Boolean cval = (Boolean) qo.calculate();
        if(cval){
            return qo.getRight();
        }
        return calculateItem(right);
    }
    public String fetchSelf() {
        return ":";
    }

}
