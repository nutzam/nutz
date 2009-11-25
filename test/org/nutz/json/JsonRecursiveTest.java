package org.nutz.json;

import static org.junit.Assert.*;

import org.junit.Test;

import org.nutz.lang.Each;
import org.nutz.lang.ExitLoop;
import org.nutz.lang.Lang;

public class JsonRecursiveTest {

	public static class R {

		public R() {}

		public R(int id, R r) {
			this.id = id;
			this.recur = r;
		}

		public int id;
		public R recur;
	}

	@Test
	public void testSimpleRecur() {
		R r1 = new R(1, null);
		R r2 = new R(2, r1);
		r1.recur = r2;
		R rr = Json.fromJson(R.class, Lang.inr(Json.toJson(r1)));
		assertEquals(r1.id, rr.id);
		assertEquals(r2.id, rr.recur.id);
		assertNull(rr.recur.recur);
	}

	public static class Node {

		public Node() {}

		public Node(int id, Node parent) {
			this.id = id;
			this.parent = parent;
		}

		public int id;
		public Node parent;
		public Node[] children;
	}

	@Test
	public void testSimpleNode() {
		int i = 1;
		Node root = new Node(i++, null);
		Node chd1 = new Node(i++, root);
		Node chd11 = new Node(i++, chd1);
		Node chd12 = new Node(i++, chd1);
		Node chd2 = new Node(i++, root);
		root.children = Lang.array(chd1, chd2);
		chd1.children = Lang.array(chd11, chd12);

		String json = Json.toJson(root, JsonFormat.nice());
		final Node nd = Json.fromJson(Node.class, Lang.inr(json));
		assertEquals(root.id, nd.id);
		assertEquals(root.children.length, nd.children.length);
		Lang.each(root.children, new Each<Node>() {
			public void invoke(int index, Node ele, int size) throws ExitLoop {
				assertNull(nd.children[index].parent);
				assertEquals(ele.id, nd.children[index].id);
			}
		});
		Lang.each(root.children[0].children, new Each<Node>() {
			public void invoke(int index, Node ele, int size) throws ExitLoop {
				assertNull(nd.children[index].parent);
				assertEquals(ele.id, nd.children[0].children[index].id);
			}
		});
	}
}
