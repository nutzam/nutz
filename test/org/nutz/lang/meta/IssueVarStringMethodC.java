package org.nutz.lang.meta;

import java.util.Arrays;
import java.util.List;

public class IssueVarStringMethodC {

    private IssueVarStringMethodC(){}
    
    public List<String> names;
    
    public static IssueVarStringMethodC make(String...names) {
        IssueVarStringMethodC sm = new IssueVarStringMethodC();
        sm.names = Arrays.asList(names);
        return sm;
    }
}
