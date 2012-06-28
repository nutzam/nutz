package org.nutz.dao.impl;

import org.nutz.dao.entity.LinkVisitor;

public abstract class AbstractLinkVisitor implements LinkVisitor {

    protected EntityOperator opt;

    public LinkVisitor opt(EntityOperator opt) {
        this.opt = opt;
        return this;
    }

}
