package org.nutz.json.meta;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import org.nutz.castor.Castors;
import org.nutz.json.JsonField;
import org.nutz.lang.util.NutType;
import org.nutz.mapl.Mapl;

public class TreeNode {
    
    private String name;
    
    @JsonField(createBy="makeChildren")
    private List<TreeNode> children;
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }

    public List<TreeNode> getChildren() {
        return children;
    }
    
    public void setChildren(List<TreeNode> children) {
        this.children = children;
    }
    
    public Object makeChildren(Type type, Object obj) {
        return Mapl.maplistToObj(obj, NutType.list(getClass()));
    }
}
