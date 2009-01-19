package com.zzh.dao.entity;

import com.zzh.dao.entity.Entity;
import com.zzh.dao.entity.EntityField;
import com.zzh.dao.entity.EntityHolder;
import com.zzh.dao.entity.analyzer.OneEntity;
import com.zzh.dao.entity.analyzer.TableEntity;
import com.zzh.lang.types.Castors;

import junit.framework.TestCase;

public class EntityHolderTest extends TestCase {

	private EntityHolder holder;

	@Override
	protected void setUp() throws Exception {
		holder = new EntityHolder(Castors.me());
	}

	public void testNormal() {
		Entity<OneEntity> em = holder.getEntity(OneEntity.class);
		assertNull(em);
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
		assertFalse(ef.isPk());
		assertTrue(ef.isNotNull());
		assertFalse(ef.isUnique());
		assertFalse(ef.isUnsigned());
	}
}
