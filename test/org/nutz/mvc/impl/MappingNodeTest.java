package org.nutz.mvc.impl;

import static org.junit.Assert.*;

import org.junit.Test;
import org.nutz.mvc.ActionContext;

public class MappingNodeTest {

    @Test
    public void test_simple_mapping() {
        MappingNode<String> root = new MappingNode<String>();
        root.add("/a/b/c", "A");
        root.add("/a/c/", "B");
        root.add("/a/f", "C");
        root.add("/a", "D");

        ActionContext ac = new ActionContext();
        assertEquals("A", root.get(ac, "/a/b/c"));
        assertEquals("/a/b/c", ac.getPath());

        assertEquals("B", root.get(ac, "/a/c"));
        assertEquals("/a/c", ac.getPath());

        assertEquals("C", root.get(ac, "/a/f/"));
        assertEquals("/a/f/", ac.getPath());

        assertEquals("D", root.get(ac, "/a/"));
        assertEquals("/a/", ac.getPath());

        assertNull(root.get(ac, "/a/x"));
        assertEquals("/a/x", ac.getPath());
    }

    @Test
    public void test_single_path_arg() {
        MappingNode<String> root = new MappingNode<String>();
        root.add("/a/?/c", "A");

        ActionContext ac = new ActionContext();
        assertEquals("A", root.get(ac, "/a/b/c"));
        assertEquals(1, ac.getPathArgs().size());
        assertEquals("b", ac.getPathArgs().get(0));
    }

    @Test
    public void test_multi_path_arg() {
        MappingNode<String> root = new MappingNode<String>();
        root.add("/a/*", "A");

        ActionContext ac = new ActionContext();
        assertEquals("A", root.get(ac, "/a"));
        assertEquals(0, ac.getPathArgs().size());

        assertEquals("A", root.get(ac, "/a/b/c"));
        assertEquals(2, ac.getPathArgs().size());
        assertEquals("b", ac.getPathArgs().get(0));
        assertEquals("c", ac.getPathArgs().get(1));
    }

    @Test
    public void test_single_and_multi_path_arg() {
        MappingNode<String> root = new MappingNode<String>();
        root.add("/a/?/c/*", "A");

        ActionContext ac = new ActionContext();

        assertEquals("A", root.get(ac, "/a/b/c"));
        assertEquals(1, ac.getPathArgs().size());
        assertEquals("b", ac.getPathArgs().get(0));

        assertEquals("A", root.get(ac, "/a/b/c/d"));
        assertEquals(2, ac.getPathArgs().size());
        assertEquals("b", ac.getPathArgs().get(0));
        assertEquals("d", ac.getPathArgs().get(1));

        assertEquals("A", root.get(ac, "/a/b/c/d/e"));
        assertEquals(3, ac.getPathArgs().size());
        assertEquals("b", ac.getPathArgs().get(0));
        assertEquals("d", ac.getPathArgs().get(1));
        assertEquals("e", ac.getPathArgs().get(2));

    }

}
