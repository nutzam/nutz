package org.nutz.dao.impl.entity.info;

import org.nutz.dao.entity.annotation.Comment;
import org.nutz.dao.entity.annotation.PK;
import org.nutz.dao.entity.annotation.Table;
import org.nutz.dao.entity.annotation.TableIndexes;
import org.nutz.dao.entity.annotation.TableMeta;
import org.nutz.dao.entity.annotation.View;

public class TableInfo {

    public Table annTable;

    public View annView;

    public TableMeta annMeta;

    public PK annPK;

    public TableIndexes annIndexes;

    public Comment tableComment;

}
