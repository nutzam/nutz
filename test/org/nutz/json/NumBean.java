package org.nutz.json;

/**
 *
 * @author hxy
 */
public class NumBean {
    @JsonField(dataFormat="00.00")
    private int num1;
    
    @JsonField(dataFormat="00.00")
    private Integer num2 = 2;
    
    public int getNum1() {
        return num1;
    }

    public void setNum1(int num1) {
        this.num1 = num1;
    }

    
}
