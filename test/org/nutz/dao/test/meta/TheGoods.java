package org.nutz.dao.test.meta;

import org.nutz.dao.entity.annotation.*;
import org.nutz.lang.Files;

/**
 * 测试一下各种二进制数据结构
 * 
 * @author zozoh(zozohtnt@gmail.com)
 */
@Table("t_goods")
public class TheGoods {

    public static TheGoods create(String name, String thumbPath) {
        TheGoods tg = new TheGoods();
        tg.setName(name);
        tg.setThumbnail(Files.readBytes(thumbPath));
        return tg;
    }

    @Id
    private int id;

    @Name
    private String name;

    @Column("thumb")
    private byte[] thumbnail;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public byte[] getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(byte[] thumbnail) {
        this.thumbnail = thumbnail;
    }

}
