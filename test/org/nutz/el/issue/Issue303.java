package org.nutz.el.issue;

public class Issue303 {
    public String name;
    public Issue303 child;
    
    public Issue303(String name) {
        this.name = name;
    }
    
    public String getName(){
        return name;
    }
}
