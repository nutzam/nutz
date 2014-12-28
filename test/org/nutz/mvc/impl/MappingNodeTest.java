package org.nutz.mvc.impl;

import static org.junit.Assert.*;

import org.junit.Test;
import org.nutz.lang.Lang;
import org.nutz.mvc.ActionContext;

public class MappingNodeTest {

    @Test
    public void test_remain() {
        MappingNode<String> root = new MappingNode<String>();
        root.add("/a/?/?", "A");
        root.add("/a/**", "B");
        root.add("/a/x", "C");
        root.add("/a/?/m/**", "D");

        ActionContext ac = new ActionContext();
        assertEquals("A", root.get(ac, "/a/b/c"));
        assertEquals("/a/b/c", ac.getPath());
        assertEquals("b,c", Lang.concat(",", ac.getPathArgs()).toString());

        assertEquals("B", root.get(ac, "/a/c/d/e"));
        assertEquals("/a/c/d/e", ac.getPath());
        assertEquals(1, ac.getPathArgs().size());
        assertEquals("c/d/e", ac.getPathArgs().get(0));

        assertEquals("C", root.get(ac, "/a/x/"));
        assertEquals("/a/x/", ac.getPath());
        assertEquals("", Lang.concat(",", ac.getPathArgs()).toString());

        assertEquals("D", root.get(ac, "/a/c/m/x/y/z"));
        assertEquals("/a/c/m/x/y/z", ac.getPath());
        assertEquals(2, ac.getPathArgs().size());
        assertEquals("c", ac.getPathArgs().get(0));
        assertEquals("x/y/z", ac.getPathArgs().get(1));
    }

    @Test
    public void test_quesmark_asterisk() {
        MappingNode<String> root = new MappingNode<String>();
        root.add("/*", "root");
        root.add("/a/?/?", "A");
        root.add("/a/*", "B");
        root.add("/a/x", "C");
        root.add("/x/?/*", "D");
        root.add("/m/?/**", "E");

        ActionContext ac = new ActionContext();
        assertEquals("A", root.get(ac, "/a/b/c"));
        assertEquals("/a/b/c", ac.getPath());
        assertEquals("b,c", Lang.concat(",", ac.getPathArgs()).toString());

        assertEquals("B", root.get(ac, "/a/c/d/e"));
        assertEquals("/a/c/d/e", ac.getPath());
        assertEquals("c,d,e", Lang.concat(",", ac.getPathArgs()).toString());

        assertEquals("C", root.get(ac, "/a/x/"));
        assertEquals("/a/x/", ac.getPath());
        assertEquals("", Lang.concat(",", ac.getPathArgs()).toString());

        assertEquals("D", root.get(ac, "/x/a/o/p"));
        assertEquals("/x/a/o/p", ac.getPath());
        assertEquals("a,o,p", Lang.concat(",", ac.getPathArgs()).toString());

        assertEquals("D", root.get(ac, "/x/a"));
        assertEquals("/x/a", ac.getPath());
        assertEquals("a", Lang.concat(",", ac.getPathArgs()).toString());

        assertEquals("E", root.get(ac, "/m/a/o/p"));
        assertEquals("/m/a/o/p", ac.getPath());
        assertEquals("a,o/p", Lang.concat(",", ac.getPathArgs()).toString());

        assertEquals("E", root.get(ac, "/m/a"));
        assertEquals("/m/a", ac.getPath());
        assertEquals("a", Lang.concat(",", ac.getPathArgs()).toString());
    }

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

    @Test
    public void test_issue() {
        MappingNode<String> root = new MappingNode<String>();
        root.add("/*", "A");
        root.add("/abc/wendal", "B");
        root.add("/abc/wen/?/zzz", "B");
        root.add("/abc/wen/?/zzz/*", "B");
        ActionContext ac = new ActionContext();

        assertEquals("A", root.get(ac, "/a/b/c/d/e")); // 连第一个路径都不匹配
        assertEquals("A", root.get(ac, "/abc/abc")); // 匹配第一路径,但不匹配第二路径
        assertEquals("B", root.get(ac, "/abc/wendal")); // 匹配第一个路径, 也匹配第二路径
        assertEquals("B", root.get(ac, "/abc/wen/qq/zzz")); // 匹配全部
        assertEquals("A", root.get(ac, "/abc/wen/qq/qqq")); // 最后一个路径不匹配
        assertEquals("B", root.get(ac, "/abc/wen/qq/zzz/123")); // 最后一个路径泛匹配

    }
}
