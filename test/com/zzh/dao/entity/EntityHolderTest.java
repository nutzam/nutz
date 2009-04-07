package com.zzh.dao.entity;

import com.zzh.dao.entity.Entity;
import com.zzh.dao.entity.EntityField;
import com.zzh.dao.entity.EntityHolder;
import com.zzh.dao.entity.analyzer.OneEntity;
import com.zzh.dao.entity.analyzer.TableEntity;

import junit.framework.TestCase;

public class EntityHolderTest extends TestCase {

	private EntityHolder holder;

	@Override
	protected void setUp() throws Exception {
		holder = new EntityHolder();
	}

	public void testOneEntity() {
		try {
			holder.getEntity(OneEntity.class);
			fail();
		} catch (ErrorEntitySyntaxException e) {}

		Entity<TableEntity> en = holder.getEntity(TableEntity.class);
		assertNotNull(en);
	}

	public void testTableName() {
		Entity<TableEntity> en = holder.getEntity(TableEntity.class);
		assertEquals("t_xyz", en.getTableName());
		assertEquals(8, en.fields().size());
	}

	public void testMappingName() {
		Entity<TableEntity> en = holder.getEntity(TableEntity.class);
		EntityField ef = en.getField("email");
		assertEquals("myEmail", ef.getColumnName());
		assertFalse(ef.isId());
		assertTrue(ef.isNotNull());
	}

	public static class SubTable extends TableEntity {}

	public void testSubTable() {
		Entity<SubTable> en = holder.getEntity(SubTable.class);
		assertEquals("t_xyz", en.getTableName());
		assertEquals(8, en.fields().size());
	}
}
