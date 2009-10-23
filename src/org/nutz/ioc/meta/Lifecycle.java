package org.nutz.ioc.meta;

import org.nutz.dao.entity.annotation.*;

/**
 * Each field of the object indicate a way to Ioc how client want treat the
 * object when special event happend.
 * 
 * Here is a promise between Ioc and client. If the field contains ".", it will
 * be take as a "Callback" class, it must implements the interface
 * "org.nutz.ioc.Callback". Ioc will invoke it at the special moment.
 * 
 * If it don't contains ".", Ioc will try to looking for a method same as the
 * field value. The method should without any arguments. If fail to find the
 * method, Ioc will take the string as "deposer", if fail, a RuntimeException
 * will be thrown.
 * 
 * @author zozoh
 * 
 */
@Table("nut_lifecycle")
public class Lifecycle {

	@Column
	@Id(auto = false)
	private int id;

	/**
	 * After the moment object instacne be created A singleton object only have
	 * one time be create But the un-singleton object will be create each time
	 * when be fetched
	 */
	@Column("born")
	private String create;

	/**
	 * When the object instance be destory. It only happen when the object is
	 * singleton, for the reason, only singleton object, the Ioc will hold the
	 * instance reference. And the Ioc should have the responsbility to release
	 * the object. When the Ioc decide don't hold the object referance anymore,
	 * it will call the "depose" to release the object.
	 */
	@Column
	private String depose;

	/**
	 * Before Object instance return to clent.
	 */
	@Column
	private String fetch;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getCreate() {
		return create;
	}

	public void setCreate(String create) {
		this.create = create;
	}

	public String getDepose() {
		return depose;
	}

	public void setDepose(String depose) {
		this.depose = depose;
	}

	public String getFetch() {
		return fetch;
	}

	public void setFetch(String fetch) {
		this.fetch = fetch;
	}

}
