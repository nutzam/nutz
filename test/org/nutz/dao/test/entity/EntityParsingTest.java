package org.nutz.dao.test.entity;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.nutz.dao.DaoException;
import org.nutz.dao.entity.Entity;
import org.nutz.dao.entity.MappingField;
import org.nutz.dao.impl.entity.field.ManyLinkField;
import org.nutz.dao.impl.entity.field.ManyManyLinkField;
import org.nutz.dao.test.DaoCase;
import org.nutz.dao.test.meta.Base;
import org.nutz.dao.test.meta.Pet;
import org.nutz.dao.test.meta.Platoon;
import org.nutz.dao.test.normal.Pet2;

public class EntityParsingTest extends DaoCase {

    private <T> Entity<T> en(Class<T> type) {
        return dao.getEntity(type);
    }

    @Test
    public void test_pk_and_name_in_same_pojo() {
        Entity<TO6> en = en(TO6.class);
        assertEquals(2, en.getCompositePKFields().size());
        assertEquals("id", en.getNameField().getName());
    }

    @Test
    public void test_extends_tables_name() {
        Entity<?> en = en(Pet.class);
        Entity<?> en2 = en(Pet2.class);
        assertEquals(en.getTableName(), en2.getTableName());
        assertEquals(en.getViewName(), en2.getViewName());
    }

    @Test
    public void test_override_field() {
        Entity<?> en = en(Pet2.class);
        MappingField ef = en.getField("nickName");
        assertEquals("alias", ef.getColumnName());
        assertEquals(1, en.cloneBeforeInsertMacroes().size());
    }

    @Test
    public void eval_manys() {
        Entity<?> en = en(Base.class);
        ManyLinkField link = (ManyLinkField) en.getLinkFields("platoons").get(0);
        assertEquals("platoons", link.getName());
        assertEquals("org.nutz.dao.test.meta.Platoon", link.getLinkedEntity().getType().getName());
        assertEquals("baseName", link.getLinkedField().getName());
        assertEquals("name", link.getHostField().getName());
    }

    @Test
    public void eval_manys_with_null_field() {
        Entity<?> en = en(Base.class);
        ManyLinkField link = (ManyLinkField) en.getLinkFields("wavebands").get(0);
        assertEquals("wavebands", link.getName());
        assertEquals("org.nutz.dao.test.meta.WaveBand", link.getLinkedEntity().getType().getName());
        assertNull(link.getLinkedField());
        assertNull(link.getHostField());
    }

    @Test
    public void eval_manymany() {
        Entity<?> en = en(Base.class);
        ManyManyLinkField link = (ManyManyLinkField) en.getLinkFields("fighters").get(0);
        assertEquals("dao_m_base_fighter", link.getRelationName());
    }

    @Test
    public void eval_id_name() {
        Entity<?> en = en(Platoon.class);
        assertEquals("id", en.getIdField().getName());
        assertEquals("name", en.getNameField().getName());
    }

    @Test
    public void test_pk_multiple() {
        Entity<?> en = en(TO0.class);
        assertEquals(2, en.getCompositePKFields().size());
        assertEquals("t_o0", en.getViewName());
        assertEquals("id", en.getCompositePKFields().get(0).getName());
        assertEquals("name", en.getCompositePKFields().get(1).getName());
        assertNull(en.getIdField());
        assertNull(en.getNameField());

        assertTrue(en.getField("id").isCompositePk());
        assertTrue(en.getField("name").isCompositePk());
    }

    @Test
    public void test_pk_id() {
        Entity<?> en = en(TO1.class);
        assertTrue(en.getCompositePKFields().isEmpty());
        assertEquals("t_o1", en.getViewName());
        assertEquals("id", en.getIdField().getName());
        assertEquals("name", en.getNameField().getName());

        assertFalse(en.getField("id").isCompositePk());
        assertFalse(en.getField("name").isCompositePk());
    }

    @Test
    public void test_pk_name() {
        Entity<?> en = en(TO2.class);
        assertTrue(en.getCompositePKFields().isEmpty());
        assertEquals("t_o2", en.getViewName());
        assertEquals("id", en.getIdField().getName());
        assertEquals("name", en.getNameField().getName());

        assertFalse(en.getField("id").isCompositePk());
        assertFalse(en.getField("name").isCompositePk());
    }

    @Test
    public void test_pk_order() {
        Entity<?> en = en(TO4.class);
        assertEquals(2, en.getCompositePKFields().size());
        assertEquals("t_o4", en.getViewName());
        assertEquals("masterId", en.getCompositePKFields().get(0).getName());
        assertEquals("id", en.getCompositePKFields().get(1).getName());
        assertNull(en.getIdField());
        assertNull(en.getNameField());

        assertTrue(en.getField("masterId").isCompositePk());
        assertTrue(en.getField("id").isCompositePk());
    }

    @Test
    public void test_complex_pojo_without_db() {
        Entity<?> en = en(TO5.class);
        assertEquals("toid", en.getField("id").getColumnName());
    }

    @Test(expected=DaoException.class)
    public void test_entity_with_two_id() {
    	dao.create(ErrPet.class, true);
    }

    @Test
    public void test_anno_prefix_suffix() {
        Entity<?> en = en(TO7.class);
        assertEquals("t_testnut", en.getTableName());
        assertEquals("c_id_int", en.getIdField().getColumnName());
        assertEquals("c_to7_name_str", en.getNameField().getColumnName());
        assertEquals("c_to7_age_int", en.getField("age").getColumnName());
        assertEquals("addr", en.getField("addr").getColumnName());
    }
}
