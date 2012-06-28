package org.nutz.ioc.java;

import org.nutz.lang.Lang;
import org.nutz.lang.Strings;
import org.nutz.lang.util.LinkedArray;
import org.nutz.lang.util.LinkedCharArray;

public class ChainParsing {

    private char[] cs;
    private int i;
    private ChainNode first;
    private ChainNode last;
    /**
     * 如果不为 null，那么 addNode 将全加到这里
     */
    private LinkedArray<LinkedArray<ChainNode>> argss;
    /**
     * 控制 parse 函数，合适退出
     */
    private LinkedCharArray ends;
    /**
     * parse 函数所使用的字符缓冲区
     */
    private StringBuilder sb;

    public ChainParsing(String s) {
        ends = new LinkedCharArray(10);
        sb = new StringBuilder();
        argss = new LinkedArray<LinkedArray<ChainNode>>(5);
        cs = s.toCharArray();
        parse();
    }

    private void parse() {
        for (; i < cs.length; i++) {
            char c = cs[i];
            if (c == ',') {
                checkIfNeedAddNode();
            }
            // String
            else if (c == '\'' || c == '"') {
                clearStringBuffer();
                for (i++; i < cs.length; i++) {
                    char n = cs[i];
                    if (n == c)
                        break;
                    sb.append(n);
                }
                addNode(new StringNode(clearStringBuffer()));
            }
            // @Ioc | @Context | @Name
            else if (c == '@') {
                String name = readToDot().toUpperCase();
                if ("IOC".equals(name)) {
                    addNode(new IocSelfNode());
                } else if ("CONTEXT".equals(name)) {
                    addNode(new IocContextNode());
                } else if ("NAME".equals(name)) {
                    addNode(new IocObjectNameNode());
                }
                continue;
            }
            // $xxx
            else if (c == '$') {
                String name = readToDot();
                addNode(new IocObjectNode(name));
                continue;
            }
            // (...) in func
            else if (c == '(') {
                String funcName = Strings.trim(clearStringBuffer());
                argss.push(new LinkedArray<ChainNode>(ChainNode.class, 5));
                ends.push(')');
                i++;
                parse();
                ends.popLast();
                ChainNode[] args = argss.popLast().toArray();
                int pos = funcName.lastIndexOf('.');
                if (pos > 0) {
                    String className = funcName.substring(0, pos);
                    funcName = funcName.substring(pos + 1);
                    addNode(new StaticFunctionNode(className, funcName, args));
                } else {
                    addNode(new ObjectFunctionNode(funcName, args));
                }
                clearStringBuffer();
            }
            // If funcs, it will will quit when encounter the ')'
            else if (ends.size() > 0 && c == ends.last()) {
                checkIfNeedAddNode();
                return;
            }
            // xxx.xxx.xxx
            else {
                sb.append(c);
            }
        }
        checkIfNeedAddNode();
    }

    private void checkIfNeedAddNode() {
        // Finished Parsing, check the buffer
        if (!Strings.isBlank(sb)) {
            String s = Strings.trim(clearStringBuffer());
            // null
            if (s.equalsIgnoreCase("null")) {
                addNode(new NullNode());
            }
            // boolean
            else if (s.matches("^(true|false)$")) {
                addNode(new BooleanNode(s));
            }
            // number
            else if (s.matches("^([0-9]+)$")) {
                addNode(new NumberNode(s));
            }
            // the chain is empty
            else if (null == last) {
                int pos = s.lastIndexOf('.');
                if (pos < 0)
                    throw Lang.makeThrow("Don't know how to invoke '%s'", s);
                String className = s.substring(0, pos);
                String funcName = s.substring(pos + 1);
                addNode(new StaticFunctionNode(className, funcName, new ChainNode[0]));
            }
            // some node had been built
            else {
                addNode(new FieldNode(s));
            }
        }
    }

    private String readToDot() {
        for (i++; i < cs.length; i++) {
            char c = cs[i];
            if (c == '.' || c == ',')
                break;
            sb.append(c);
        }
        return clearStringBuffer();
    }

    private String clearStringBuffer() {
        String re = Strings.trim(sb);
        sb = new StringBuilder();
        return re;
    }

    private void addNode(ChainNode node) {
        // For arguments
        if (argss.size() > 0) {
            argss.last().push(node);
        }
        // First time
        else if (last == null) {
            first = node;
            last = node;
        }
        // Normal ...
        else {
            last.setNext(node);
            last = node;
        }
    }

    public ChainNode getNode() {
        return first;
    }

}
