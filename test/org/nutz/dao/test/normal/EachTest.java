package org.nutz.dao.test.normal;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.nutz.dao.Cnd;
import org.nutz.dao.entity.Record;
import org.nutz.dao.test.DaoCase;
import org.nutz.dao.test.meta.Pet;
import org.nutz.dao.util.cri.SimpleCriteria;
import org.nutz.lang.Each;
import org.nutz.lang.Lang;
import org.nutz.lang.Loop;
import org.nutz.lang.LoopException;

public class EachTest extends DaoCase {

    public void before() {
        dao.create(Pet.class, true);
        // Insert 8 records
        for (int i = 0; i < 8; i++)
            dao.insert(Pet.create("pet" + i));
    }

    @Test
    public void each_by_loop() {
        final List<Pet> pets = new ArrayList<Pet>();
        final String[] ss = new String[2];
        int re = dao.each(Pet.class, Cnd.cri().asc("id"), null, new Loop<Pet>() {

            public boolean begin() throws LoopException {
                ss[0] = "begin";
                return true;
            }

            public void invoke(int i, Pet pet, int length) {
                pets.add(pet);
            }

            public void end() throws LoopException {
                ss[1] = "end";
            }
        });
        assertEquals(8, re);
        assertEquals(re, pets.size());
        assertEquals("begin", ss[0]);
        assertEquals("end", ss[1]);
    }

    @Test
    public void each_by_orderbyset() {
        final List<Pet> pets = new ArrayList<Pet>();
        int re = dao.each(Pet.class, Cnd.cri().asc("id"), null, new Each<Pet>() {
            public void invoke(int i, Pet pet, int length) {
                pets.add(pet);
            }
        });
        assertEquals(8, re);
        assertEquals(re, pets.size());

        pets.clear();
        re = dao.each(Pet.class, Cnd.cri().asc("id"), null, new Each<Pet>() {
            public void invoke(int index, Pet pet, int length) {
                pets.add(pet);
                if (index > 0)
                    Lang.Break();
            }
        });
        assertEquals(2, re);
        assertEquals(re, pets.size());

        pets.clear();
        re = dao.each(Pet.class, Cnd.cri().asc("id"), dao.createPager(0, 3), new Each<Pet>() {
            public void invoke(int index, Pet pet, int length) {
                pets.add(pet);
            }
        });
        assertEquals(8, re);
        assertEquals(re, pets.size());

    }

    @Test
    public void each_by_map_idkeyset() {
        final List<Pet> pets = new ArrayList<Pet>();
        dao.each(Pet.class, null, null, new Each<Pet>() {
            public void invoke(int i, Pet pet, int length) {
                pets.add(pet);
            }
        });
        Map<Integer, Pet> map = new HashMap<Integer, Pet>();
        map.put(pets.get(2).getId(), null);
        map.put(pets.get(4).getId(), null);
        assertEquals(2, map.size());

        pets.clear();
        dao.each(Pet.class, Cnd.where("id", "in", map.keySet()), null, new Each<Pet>() {
            public void invoke(int i, Pet pet, int length) {
                pets.add(pet);
            }
        });
        assertEquals(2, pets.size());
    }

    @Test
    public void each_by_map_namekeyset() {
        Map<String, Pet> map = new HashMap<String, Pet>();
        map.put("pet3", null);
        map.put("pet5", null);
        final List<Pet> pets = new ArrayList<Pet>();
        dao.each(Pet.class, Cnd.where("name", "in", map.keySet()), null, new Each<Pet>() {
            public void invoke(int i, Pet pet, int length) {
                pets.add(pet);
            }
        });
        assertEquals(2, pets.size());
    }

    @Test
    public void each_by_int_range() {
        int maxId = dao.getMaxId(Pet.class);
        final List<Pet> pets = new ArrayList<Pet>();
        dao.each(    Pet.class,
                    Cnd.where("id", "IN", new int[]{maxId, maxId - 1}),
                    null,
                    new Each<Pet>() {
                        public void invoke(int i, Pet pet, int length) {
                            pets.add(pet);
                        }
                    });
        assertEquals(2, pets.size());
    }

    @Test
    public void each_by_long_range() {
        int maxId = dao.getMaxId(Pet.class);
        final List<Pet> pets = new ArrayList<Pet>();
        dao.each(    Pet.class,
                    Cnd.where("id", "IN", new long[]{maxId, maxId - 1}),
                    null,
                    new Each<Pet>() {
                        public void invoke(int i, Pet pet, int length) {
                            pets.add(pet);
                        }
                    });
        assertEquals(2, pets.size());
    }

    @Test
    public void each_by_special_char() {
        dao.update(dao.fetch(Pet.class).setName("a@b"));
        final List<Pet> pets = new ArrayList<Pet>();
        dao.each(Pet.class, Cnd.where("name", "=", "a@b"), null, new Each<Pet>() {
            public void invoke(int i, Pet pet, int length) {
                pets.add(pet);
            }
        });
        assertEquals(1, pets.size());
    }

    @Test
    public void each_by_special_char2() {
        dao.update(dao.fetch(Pet.class).setName("a$b"));
        final List<Pet> pets = new ArrayList<Pet>();
        dao.each(Pet.class, Cnd.where("name", "=", "a$b"), null, new Each<Pet>() {
            public void invoke(int i, Pet pet, int length) {
                pets.add(pet);
            }
        });
        assertEquals(1, pets.size());
    }

    @Test
    public void each_by_pager() {
        final List<Pet> pets = new ArrayList<Pet>();
        dao.each(Pet.class, Cnd.orderBy().asc("name"), dao.createPager(3, 2), new Each<Pet>() {
            public void invoke(int i, Pet pet, int length) {
                pets.add(pet);
            }
        });
        assertEquals(2, pets.size());
        assertEquals("pet4", pets.get(0).getName());
        assertEquals("pet5", pets.get(1).getName());
    }

    @Test
    public void each_by_like() {
        final List<Pet> pets = new ArrayList<Pet>();
        dao.each(    Pet.class,
                    Cnd.where("name", "LIKE", "6"),
                    dao.createPager(1, 10),
                    new Each<Pet>() {
                        public void invoke(int i, Pet pet, int length) {
                            pets.add(pet);
                        }
                    });
        assertEquals(1, pets.size());
        assertEquals("pet6", pets.get(0).getName());
    }

    @Test
    public void each_by_like_ignorecase() {
        SimpleCriteria cri = Cnd.cri();
        cri.where().andLike("name", "PeT6", true);
        final List<Pet> pets = new ArrayList<Pet>();
        dao.each(Pet.class, cri, dao.createPager(1, 10), new Each<Pet>() {
            public void invoke(int i, Pet pet, int length) {
                pets.add(pet);
            }
        });
        assertEquals(1, pets.size());
        assertEquals("pet6", pets.get(0).getName());
    }

    @Test
    public void query_records() {
        final List<Record> pets = new ArrayList<Record>();
        dao.each("t_pet", Cnd.orderBy().asc("name"), dao.createPager(1, 10), new Each<Record>() {
            public void invoke(int i, Record pet, int length) {
                pets.add(pet);
            }
        });
        assertEquals(8, pets.size());
        assertEquals("pet0", pets.get(0).get("name"));
        assertEquals("pet7", pets.get(7).get("name"));
    }

    @Test
    public void query_records_pager() {
        final List<Record> pets = new ArrayList<Record>();
        dao.each("t_pet:id", Cnd.orderBy().asc("name"), dao.createPager(3, 2), new Each<Record>() {
            public void invoke(int i, Record pet, int length) {
                pets.add(pet);
            }
        });
        assertEquals(2, pets.size());
        assertEquals("pet4", pets.get(0).get("name"));
        assertEquals("pet5", pets.get(1).get("name"));
    }

}
