package org.nutz.castor.model;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * create by zhouwenqing 2017/7/27 .
 */
public class BaseTreeEntity<ID, E extends BaseEntity<ID>> extends BaseEntity<ID> implements Serializable {
    private static final long serialVersionUID = 74110155499217292L;

    private E parent;
    private List<E> children;
    private Map<ID, E> childMap;

    public E getParent() {
        return parent;
    }

    public void setParent(E parent) {
        this.parent = parent;
    }

    public List<E> getChildren() {
        return children;
    }

    public void setChildren(List<E> children) {
        this.children = children;
    }

    public Map<ID, E> getChildMap() {
        return childMap;
    }

    public void setChildMap(Map<ID, E> childMap) {
        this.childMap = childMap;
    }
}
