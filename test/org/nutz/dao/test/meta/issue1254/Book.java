package org.nutz.dao.test.meta.issue1254;

import java.util.List;

import org.nutz.dao.entity.annotation.ColDefine;
import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.Id;
import org.nutz.dao.entity.annotation.ManyMany;
import org.nutz.dao.entity.annotation.Table;

@Table("t_issue1254_book")
public class Book {
    @Id
    @ColDefine(customType = "INT(11)")
    private Long id;

    @Column
    private String title;

    @ManyMany(relation = "t_issue1254_book_tag", from = "book_id", to = "tag_id")
    private List<Tag> tagList;

    public Long getId() {
        return id;
    }

    public Book setId(Long id) {
        this.id = id;
        return this;
    }

    public String getTitle() {
        return title;
    }

    public Book setTitle(String title) {
        this.title = title;
        return this;
    }

    public List<Tag> getTagList() {
        return tagList;
    }

    public Book setTagList(List<Tag> tagList) {
        this.tagList = tagList;
        return this;
    }
}
