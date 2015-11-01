package org.nutz.dao.test.meta.issue918;

import java.util.List;

import org.nutz.dao.entity.annotation.ColDefine;
import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.Comment;
import org.nutz.dao.entity.annotation.Default;
import org.nutz.dao.entity.annotation.Id;
import org.nutz.dao.entity.annotation.Index;
import org.nutz.dao.entity.annotation.Readonly;
import org.nutz.dao.entity.annotation.TableIndexes;

/**
 * @author Young 树结构 http://www.cnblogs.com/yongzhi/articles/1187149.html
 */
@TableIndexes({
	@Index(fields = { "parentId", "name"}, name = "parentId_name",unique=true),
	@Index(fields = { "lft", "rgt"}, name = "lft_rgt",unique=true),
})
public abstract class AbstractTree<T> {
	
	//private static final long serialVersionUID = 1L;
	
	@Id
	private long id;
	
	@Column
	@ColDefine(notNull = true)
	@Comment("节点名称")
	private String name;
	
	@Column("p_id")
	@Comment("父节点id")
	private long parentId;		
	
	@Column
	@Comment("左节点")
	private long lft;
	
	@Column
	@Comment("右节点")
	private long rgt;
	@Column
	@Default("1")
	private Integer status;
	private T parent;	
	private List<T> children;
	
	@Readonly
	@Column
	private int level;
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public long getParentId() {
		return parentId;
	}
	public void setParentId(long parentId) {
		this.parentId = parentId;
	}
	public long getLft() {
		return lft;
	}
	public void setLft(long lft) {
		this.lft = lft;
	}
	public long getRgt() {
		return rgt;
	}
	public void setRgt(long rgt) {
		this.rgt = rgt;
	}
	public Integer getStatus() {
		return status;
	}
	public void setStatus(Integer status) {
		this.status = status;
	}
	public T getParent() {
		return parent;
	}
	public void setParent(T parent) {
		this.parent = parent;
	}
	public List<T> getChildren() {
		return children;
	}
	public void setChildren(List<T> children) {
		this.children = children;
	}
	public int getLevel() {
		return level;
	}
	public void setLevel(int level) {
		this.level = level;
	}
	
}