package org.nutz.lang.util;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.nutz.lang.Each;
import org.nutz.lang.Lang;
import org.nutz.lang.Strings;

public class SimpleNode<T> implements Node<T> {

    public SimpleNode() {}

    private T obj;
    private SimpleNode<T> parent;
    private SimpleNode<T> prev;
    private SimpleNode<T> next;
    private SimpleNode<T> firstChild;
    private SimpleNode<T> lastChild;

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
        SimpleNode<T> nd = (SimpleNode<T>) node;
        this.prev = nd;
        nd.next = this;
        nd.parent = parent;
        return this;
    }

    public Node<T> next() {
        return next;
    }

    public Node<T> next(Node<T> node) {
        SimpleNode<T> nd = (SimpleNode<T>) node;
        this.next = nd;
        nd.prev = this;
        nd.parent = this;
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

    public List<Node<T>> parents() {
        LinkedList<Node<T>> list = new LinkedList<Node<T>>();
        Node<T> me = parent;
        while (me != null) {
            list.addFirst(me);
            me = me.parent();
        }
        return list;
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
        int re = 0;
        Node<T> nd = this;
        while (null != nd.parent()) {
            re++;
            nd = nd.parent();
        }
        return re;
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
        parent = (SimpleNode<T>) node;
        node.add(this);
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
            SimpleNode<T> node = (SimpleNode<T>) nodes[0];
            node.parent = this;
            if (!this.hasChild()) {
                firstChild = node;
                lastChild = node;
                node.next = null;
                node.prev = null;
            } else {
                lastChild.next = node;
                node.prev = lastChild;
                node.next = null;
                lastChild = node;
            }
        } else {
            SimpleNode<T> theNode = (SimpleNode<T>) nodes[0];
            theNode.parent = this;
            theNode.next = (SimpleNode<T>) nodes[1];
            // 加入子节点链表
            if (null == lastChild) {
                firstChild = theNode;
            } else {
                lastChild.next = theNode;
            }
            // 循环添加
            int i = 1;
            for (; i < nodes.length - 1; i++) {
                SimpleNode<T> node = (SimpleNode<T>) nodes[i];
                node.parent = this;
                node.prev = (SimpleNode<T>) nodes[i - 1];
                node.next = (SimpleNode<T>) nodes[i + 1];
            }
            lastChild = (SimpleNode<T>) nodes[i];
            lastChild.parent = this;
            lastChild.prev = (SimpleNode<T>) nodes[i - 1];

        }
        return this;
    }

    public Node<T> addFirst(Node<T> node) {
        ((SimpleNode<T>) node).parent = this;
        if (!this.hasChild()) {
            firstChild = (SimpleNode<T>) node;
            lastChild = (SimpleNode<T>) node;
            ((SimpleNode<T>) node).next = null;
            ((SimpleNode<T>) node).prev = null;
        } else {
            firstChild.prev = (SimpleNode<T>) node;
            ((SimpleNode<T>) node).next = firstChild;
            ((SimpleNode<T>) node).prev = null;
            firstChild = (SimpleNode<T>) node;
        }
        return this;
    }

    public Node<T> child(int index) {
        if (hasChild())
            return firstChild.next(index);
        return null;
    }

    @SuppressWarnings("unchecked")
    public <E extends Node<T>> void eachChild(Each<E> callback) {
        SimpleNode<T> nd = firstChild;
        int i = 0;
        while (nd != null) {
            callback.invoke(i++, (E) nd, -1);
            nd = nd.next;
            if (nd == firstChild)
                throw Lang.makeThrow("If i am here, tell me -_-!");
        }
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
        SimpleNode<T> me = (SimpleNode<T>) child(index);
        if (null != me) {
            ((SimpleNode<T>) node).next = me;
            ((SimpleNode<T>) node).prev = me.prev;
            me.prev.next = (SimpleNode<T>) node;
            me.prev = (SimpleNode<T>) node;
            ((SimpleNode<T>) node).parent = this;
            if (firstChild == me)
                firstChild = (SimpleNode<T>) node;
        }
        return this;
    }

    public Node<T> pop() {
        if (!hasChild())
            return null;
        SimpleNode<T> re = lastChild;
        lastChild = lastChild.prev;
        if (null == lastChild)
            firstChild = null;
        else
            lastChild.next = null;

        re.prev = null;
        re.next = null;
        return re;
    }

    public Node<T> popFirst() {
        if (!hasChild())
            return null;
        SimpleNode<T> re = firstChild;
        firstChild = firstChild.next;
        if (null == firstChild)
            lastChild = null;
        else
            firstChild.prev = null;

        re.prev = null;
        re.next = null;
        return re;
    }

    public Node<T> removeChild(int index) {
        if (hasChild()) {
            SimpleNode<T> node = (SimpleNode<T>) child(index);
            if (null == node)
                return null;
            else if (node.isLast())
                return pop();
            else if (node.isFirst())
                return popFirst();

            node.next.prev = node.prev;
            node.prev.next = node.next;

            node.prev = null;
            node.next = null;
            return node;
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

    @Override
    public String toString(int level) {
        return toString();
    }
    
    @Override
    public void toXml(StringBuilder sb, int level) {
        sb.append(toString(level));
    }
}
