package org.nutz.lang.util;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.nutz.lang.Lang;
import org.nutz.lang.Strings;

public class SimpleNode<T> implements Node<T> {

    SimpleNode() {}

    private T obj;
    private Node<T> parent;
    private Node<T> prev;
    private Node<T> next;
    private Node<T> firstChild;
    private Node<T> lastChild;

    public T get() {
        return obj;
    }

    public Node<T> set(T obj) {
        this.obj = obj;
        return this;
    }

    public Node<T> parent() {
        return parent;
    }

    public Node<T> top() {
        if (null == parent)
            return this;
        return parent.top();
    }

    public Node<T> prev() {
        return prev;
    }

    public Node<T> prev(Node<T> node) {
        this.prev = node;
        return this;
    }

    public Node<T> next() {
        return next;
    }

    public Node<T> next(Node<T> node) {
        this.next = node;
        return this;
    }

    public boolean isRoot() {
        return null == parent;
    }

    public boolean isLast() {
        return null == next;
    }

    public boolean isFirst() {
        return null == prev;
    }

    public List<Node<T>> getAncestors() {
        List<Node<T>> list = new LinkedList<Node<T>>();
        Node<T> me = parent;
        while (me != null) {
            list.add(me);
            me = me.parent();
        }
        return list;
    }

    public int depth() {
        return getAncestors().size();
    }

    public List<Node<T>> getNextSibling() {
        List<Node<T>> list = new LinkedList<Node<T>>();
        Node<T> me = next;
        while (me != null) {
            list.add(me);
            me = me.next();
        }
        return list;
    }

    public List<Node<T>> getPrevSibling() {
        List<Node<T>> list = new LinkedList<Node<T>>();
        Node<T> me = prev;
        while (me != null) {
            list.add(me);
            me = me.prev();
        }
        return list;
    }

    public int index() {
        return getPrevSibling().size();
    }

    public List<Node<T>> getChildren() {
        List<Node<T>> list = new LinkedList<Node<T>>();
        if (null != firstChild) {
            list.add(firstChild);
            list.addAll(firstChild.getNextSibling());
        }
        return list;
    }

    public int countChildren() {
        int re = 0;
        if (null != firstChild) {
            Node<T> me = firstChild;
            while (me != null) {
                re++;
                me = me.next();
            }
        }
        return re;
    }

    public boolean hasChild() {
        return null != firstChild;
    }

    public Node<T> firstChild() {
        return firstChild;
    }

    public Node<T> lastChild() {
        return lastChild;
    }

    public Node<T> parent(Node<T> node) {
        parent = node;
        return this;
    }

    public Node<T> clearChildren() {
        firstChild = null;
        lastChild = null;
        return this;
    }

    @SuppressWarnings("unchecked")
    public Node<T> add(Node<?>... nodes) {
        if (nodes.length == 0) {
            return this;
        }
        if (nodes.length == 1) {
            Node<T> node = (Node<T>) nodes[0];
            node.parent(this);
            if (!this.hasChild()) {
                firstChild = node;
                lastChild = node;
                node.next(null);
                node.prev(null);
            } else {
                lastChild.next(node);
                node.prev(lastChild);
                node.next(null);
                lastChild = node;
            }
        } else {
            Node<T> theNode = (Node<T>) nodes[0];
            theNode.parent(this);
            theNode.next((Node<T>) nodes[1]);
            // 加入子节点链表
            if (null == lastChild) {
                firstChild = theNode;
            } else {
                lastChild.next(theNode);
            }
            // 循环添加
            int i = 1;
            for (; i < nodes.length - 1; i++) {
                Node<T> node = (Node<T>) nodes[i];
                node.parent(this);
                node.prev((Node<T>) nodes[i - 1]);
                node.next((Node<T>) nodes[i + 1]);
            }
            lastChild = (Node<T>) nodes[i];
            lastChild.parent(this);
            lastChild.prev((Node<T>) nodes[i - 1]);

        }
        return this;
    }

    public Node<T> addFirst(Node<T> node) {
        node.parent(this);
        if (!this.hasChild()) {
            firstChild = node;
            lastChild = node;
            node.next(null);
            node.prev(null);
        } else {
            firstChild.prev(node);
            node.next(firstChild);
            node.prev(null);
            firstChild = node;
        }
        return this;
    }

    public Node<T> child(int index) {
        if (hasChild())
            return firstChild.next(index);
        return null;
    }

    public Node<T> desc(int... indexes) {
        Node<T> me = this;
        for (int i : indexes) {
            if (!me.hasChild())
                return null;
            me = me.firstChild().next(i);
        }
        return me;
    }

    public Node<T> next(int index) {
        if (index < 0)
            return null;
        Node<T> me = this;
        while (index > 0 && me != null) {
            index--;
            me = me.next();
        }
        if (index > 0)
            return null;
        return me;
    }

    public Node<T> prev(int index) {
        Node<T> me = this;
        while (index > 0 && me != null) {
            index--;
            me = me.prev();
        }
        return me;
    }

    public Node<T> insertBefore(int index, Node<T> node) {
        Node<T> me = child(index);
        if (null != me) {
            node.next(me);
            node.prev(me.prev());
            me.prev().next(node);
            me.prev(node);
            node.parent(this);
            if (firstChild == me)
                firstChild = node;
        }
        return this;
    }

    public Node<T> pop() {
        if (!hasChild())
            return null;
        Node<T> re = lastChild;
        lastChild = lastChild.prev();
        if (null == lastChild)
            firstChild = null;
        else
            lastChild.next(null);
        return re.prev(null).next(null);
    }

    public Node<T> popFirst() {
        if (!hasChild())
            return null;
        Node<T> re = firstChild;
        firstChild = firstChild.next();
        if (null == firstChild)
            lastChild = null;
        else
            firstChild.prev(null);
        return re.prev(null).next(null);
    }

    public Node<T> removeChild(int index) {
        if (hasChild()) {
            Node<T> node = child(index);
            if (null == node)
                return null;
            else if (node.isLast())
                return pop();
            else if (node.isFirst())
                return popFirst();
            node.next().prev(node.prev());
            node.prev().next(node.next());
            return node.prev(null).next(null);
        }
        return null;
    }

    public boolean remove() {
        int i = getIndex();
        if (i < 0)
            return false;
        parent.removeChild(i);
        return true;

    }

    public int getIndex() {
        if (parent == null)
            return -1;
        int i = 0;
        Node<T> n = parent.firstChild();
        while (n != parent.child(i)) {
            i++;
        }
        return i;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        appendTo(this, sb, 0);
        return sb.toString();
    }

    static void appendTo(Node<?> node, StringBuilder sb, int depth) {
        sb.append(Strings.dup("    ", depth))
            .append(node.get() == null ? "NULL" : node.get().toString());
        Node<?> chd = node.firstChild();
        while (chd != null) {
            sb.append('\n');
            appendTo(chd, sb, depth + 1);
            chd = chd.next();
        }
    }

    static class InnerIterator<T> implements Iterator<Node<T>> {

        private Node<T> root;
        private Node<T> node;

        InnerIterator(Node<T> node) {
            this.root = node;
            if (root.hasChild())
                this.node = root.child(0);
            else
                this.node = root;
        }

        public boolean hasNext() {
            return node != root;
        }

        public Node<T> next() {
            if (node == root)
                return null;
            Node<T> re = node;
            if (node.hasChild()) {
                node = node.firstChild();
            } else if (!node.isLast()) {
                node = node.next();
            } else {
                while (node.isLast() && !node.isRoot()) {
                    node = node.parent();
                }
                if (!node.isRoot())
                    node = node.next();
            }
            return re;
        }

        public void remove() {
            throw Lang.makeThrow("No implement yet!");
        }

    }

    public Iterator<Node<T>> iterator() {
        return new InnerIterator<T>(this);
    }

}
