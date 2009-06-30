package org.nutz.dom;

import org.nutz.lang.Strings;

@SuppressWarnings("unchecked")
public abstract class Selector {

	public abstract boolean match(Node node);

	/*----------------------------------------------------------*/
	public static class NodeSelector extends Selector {
		NodeSelector(String s) {
			this.name = s;
			
		}

		private String name;
		

		@Override
		public boolean match(Node node) {
			return Strings.equalsIgnoreCase(node.getTagName(), name);
		}
	}

	/*----------------------------------------------------------*/
	public static class PathSelector extends Selector {

		@Override
		public boolean match(Node node) {
			return false;
		}

	}
}
