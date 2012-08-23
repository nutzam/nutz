package org.nutz.el.issue;

import java.util.ArrayList;
import java.util.List;

public class Issue303 {
    public String name;
    public Issue303 child;
    public List<String> list = new ArrayList<String>();
    
    public Issue303(String name) {
        this.name = name;
    }
    
    public String getName(){
        return name;
    }
}
