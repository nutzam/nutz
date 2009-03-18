package com.zzh.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.zzh.Main;
import com.zzh.dao.Dao;
import com.zzh.dao.entity.annotation.*;

import junit.framework.TestCase;

public class NotDaoTest2 extends TestCase {

	public static abstract class Animal {

		static <T extends Animal> T makeAnimal(Class<T> animalClass, String name, short age)
				throws Exception {
			T animal = animalClass.newInstance();
			animal.name = name;
			animal.age = age;
			return animal;
		}

		@Column
		@Id
		int id;
		@Column
		@Name
		String name;
		@Column
		short age;

		public int getId() {
			return id;
		}

		public void setId(int id) {
			this.id = id;
		}

		public String getName() {
			return name;
		}

		public short getAge() {
			return age;
		}

	}

	@Table("t_dog")
	public static class Dog extends Animal {

		public static Dog getDog(ResultSet rs) throws SQLException {
			Dog dog = new Dog();
			dog.id = rs.getInt("id");
			dog.name = rs.getString("name");
			dog.age = rs.getShort("age");
			return dog;
		}

		public Dog() {}

	}

	private Dao dao;

	@Override
	protected void setUp() throws Exception {
		dao = Main.getDao("com/zzh/dao/impl/test2.sqls");
		Main.prepareTables(dao);
	}

	@Override
	protected void tearDown() throws Exception {
		Main.closeDataSource();
	}

	public void testFetchDog() throws Exception {
		dao.insert(Dog.makeAnimal(Dog.class, "Jam", (short) 4));
		Dog anm = dao.fetch(Dog.class, 1);
		assertEquals("Jam", anm.getName());
		assertEquals(4, anm.getAge());
	}

	@Table("t_parrot")
	public static class Parrot extends Animal {
		protected Parrot() {}

		public Parrot(ResultSet rs) throws SQLException {
			this.id = rs.getInt("id");
			this.name = rs.getString("name");
			this.age = rs.getShort("age");
		}
	}

	public void testFetchParrot() throws Exception {
		dao.insert(Dog.makeAnimal(Parrot.class, "Pig", (short) 8));
		Parrot anm = dao.fetch(Parrot.class, 1);
		assertEquals("Pig", anm.getName());
		assertEquals(8, anm.getAge());
	}

	@Table("t_eagle")
	public static class Eagle extends Animal {
		protected Eagle() {}

		public static Eagle makeEagle() {
			return new Eagle();
		}

		public void setName(String name) {
			this.name = name;
		}

		public void setAge(short age) {
			this.age = age;
		}

	}

	public void testFetchEagle() throws Exception {
		dao.insert(Dog.makeAnimal(Eagle.class, "Black", (short) 8));
		Eagle anm = dao.fetch(Eagle.class, 1);
		assertEquals("Black", anm.getName());
		assertEquals(8, anm.getAge());
	}
	
	@Table("t_cayman")
	public static class Cayman extends Animal {
		public Cayman() {}

		public void setName(String name) {
			this.name = name;
		}

		public void setAge(short age) {
			this.age = age;
		}

	}

	public void testFetchCayman() throws Exception {
		dao.insert(Dog.makeAnimal(Cayman.class, "Mimi", (short) 20));
		Cayman anm = dao.fetch(Cayman.class, 1);
		assertEquals("Mimi", anm.getName());
		assertEquals(20, anm.getAge());
	}

}
