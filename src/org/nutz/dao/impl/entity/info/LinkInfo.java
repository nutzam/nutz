package org.nutz.dao.impl.entity.info;

import org.nutz.dao.entity.annotation.Comment;
import org.nutz.dao.entity.annotation.Many;
import org.nutz.dao.entity.annotation.ManyMany;
import org.nutz.dao.entity.annotation.One;

public class LinkInfo extends FieldInfo {

    public One one;

    public Many many;

    public ManyMany manymany;

    public Comment comment;

}
