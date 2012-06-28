package org.nutz.el;

import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;
import org.nutz.el.arithmetic.ShuntingYard;

public class RPNTest {
    private ShuntingYard sy = null;
    
    @Before
    public void setup(){
        sy = new ShuntingYard();
    }
    private String parseRPN(String val){
        StringBuilder sb = new StringBuilder();
        for(Object obj : sy.parseToRPN(val)){
            sb.append(obj);
        }
        return sb.toString();
    }
    
    @Test
    public void simpleRPN() throws IOException{
        assertEquals("11+1+", parseRPN("1+1+1"));
        assertEquals("11-", parseRPN("1-1"));
        assertEquals("11-1-", parseRPN("1-1-1"));
        assertEquals("52%1+",parseRPN("5%2+1"));
        assertEquals("152%+",parseRPN("1+5%2"));
    }
    
    @Test
    public void mulRPn() throws IOException{
        assertEquals("512+4*+3-", parseRPN("5+((1+2)*4)-3"));
        assertEquals("987*+65+412*-3+-*+", parseRPN("9+8*7+(6+5)*(-(4-1*2+3))"));
    }
}
