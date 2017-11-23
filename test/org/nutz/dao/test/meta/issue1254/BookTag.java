package org.nutz.dao.test.meta.issue1254;

import org.nutz.dao.entity.annotation.ColDefine;
import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.Id;
import org.nutz.dao.entity.annotation.Table;

@Table("t_issue1254_book_tag")
public class BookTag {
    @Id
    @ColDefine(customType = "INT(11)")
    private Long id;

    @Column("book_id")
    @ColDefine(customType = "INT(11)")
    private Long bookId;

    @Column("tag_id")
    @ColDefine(customType = "INT(11)")
    private Long tagId;

    public Long getId() {
        return id;
    }

    public BookTag setId(Long id) {
        this.id = id;
        return this;
    }

    public Long getBookId() {
        return bookId;
    }

    public BookTag setBookId(Long bookId) {
        this.bookId = bookId;
        return this;
    }

    public Long getTagId() {
        return tagId;
    }

    public BookTag setTagId(Long tagId) {
        this.tagId = tagId;
        return this;
    }
}
