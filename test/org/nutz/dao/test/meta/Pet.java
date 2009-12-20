package org.nutz.dao.test.meta;

import org.nutz.dao.entity.annotation.*;
import org.nutz.lang.Strings;

@Table("t_pet")
public class Pet {

	public static Pet create(String name) {
		Pet pet = new Pet();
		pet.setName(name);
		return pet;
	}

	public static Pet[] create(int num) {
		Pet[] pets = new Pet[num];
		for (int i = 0; i < num; i++)
			pets[i] = create("pet_" + Strings.fillHex(i, 2));
		return pets;
	}

	@Id
	private int id;

	@Name
	private String name;

	@Column("alias")
	private String nickName;

	@Column
	private int age;

	public int getId() {
		return id;
	}

	public Pet setId(int id) {
		this.id = id;
		return this;
	}

	public String getName() {
		return name;
	}

	public Pet setName(String name) {
		this.name = name;
		return this;
	}

	public String getNickName() {
		return nickName;
	}

	public Pet setNickName(String nickName) {
		this.nickName = nickName;
		return this;
	}

	public int getAge() {
		return age;
	}

	public Pet setAge(int age) {
		this.age = age;
		return this;
	}

	public String toString() {
		return name;
	}
}
