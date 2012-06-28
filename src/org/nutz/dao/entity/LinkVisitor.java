package org.nutz.dao.entity;

public interface LinkVisitor {

    void visit(Object obj, LinkField lnk);

}
