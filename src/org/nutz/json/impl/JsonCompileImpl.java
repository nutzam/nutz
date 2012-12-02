package org.nutz.json.impl;

import java.io.IOException;
import java.io.Reader;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.nutz.json.JsonException;
import org.nutz.json.JsonParser;
import org.nutz.lang.Lang;
import org.nutz.lang.Streams;
import org.nutz.lang.stream.QueueReader;
import org.nutz.mapl.MaplCompile;

/**
 * 将json理解为Map+List
 * 
 * @author wendal
 * 
 */
public class JsonCompileImpl implements JsonParser, MaplCompile<Reader> {

    private QueueReader qis;

    public Object parse(Reader reader) {
        if (reader == null)
            return null;
        this.qis = new QueueReader(reader);
        try {

            // 开始读取数据
            qis.peek();
            if (qis.isEnd()) {
                return null;
            }
            skipCommentsAndBlank();
            if (qis.peek() == 'v') {
                /*
                 * Meet the var ioc ={ maybe, try to find the '{' and break
                 */
                OUTER: while (true) {
                    qis.poll();// 尝试找到{,以确定是否为"var ioc ={"格式
                    switch (qis.peek()) {
                    case '{':
                        // case '['://还真有人这样写
                        break OUTER;
                    }
                }
            }
            return parseFromHere();
        }
        catch (IOException e) {
            throw Lang.wrapThrow(e);
        }
        // 一定要关闭文件啊，有木有
        finally {
            Streams.safeClose(reader);
        }
    }

    protected Object parseFromHere() throws IOException {
        switch (qis.peek()) {
        case '{':
            Map<String, Object> map = new LinkedHashMap<String, Object>();
            parseMap(map);
            return map;
        case '[':
            List<Object> list = new LinkedList<Object>();
            parseList(list);
            return list;
        case '"':
        case '\'':
            return parseString();// 看来是个String
        default:
            return parseSimpleType();// 其他基本数据类型
        }
    }

    /**
     * 
     * @param endTag
     *            以什么作为结束符
     */
    private String parseString() throws IOException {
        int endTag = qis.poll();
        // 直至读取到相应的结束符!
        StringBuilder sb = new StringBuilder();
        while (true) {
            if (qis.peek() == endTag) {
                qis.poll();
                break;
            }
            if (qis.peek() == '\\') {// 转义字符?
                parseSp(sb);
            } else
                sb.append((char) qis.poll());
        }
        return sb.toString();
    }

    // 读取转义字符
    private void parseSp(StringBuilder sb) throws IOException {
        qis.poll();
        switch (qis.poll()) {
        case 'n':
            sb.append('\n');
            break;
        case 'r':
            sb.append('\r');
            break;
        case 't':
            sb.append('\t');
            break;
        case '\\':
            sb.append('\\');
            break;
        case '\'':
            sb.append('\'');
            break;
        case '\"':
            sb.append('\"');
            break;
        case '/':
            sb.append('/');
            break;
        case 'u':
            char[] hex = new char[4];
            for (int i = 0; i < 4; i++)
                hex[i] = (char) qis.poll();
            sb.append((char) Integer.valueOf(new String(hex), 16).intValue());
            break;
        case 'b': // 这个支持一下又何妨?
            sb.append(' ');// 空格
            break;
        case 'f':
            sb.append('\f');// 这个支持一下又何妨?
            break;
        default:
            throw unexpectedChar(); // 1.b.37及之前的版本,会忽略非法的转义字符
        }
    }

    /**
     * 处理基本数据类型
     * 
     * @return
     */
    private Object parseSimpleType() throws IOException {
        StringBuilder sb = new StringBuilder();
        if (qis.startWith("true")) {
            qis.skip(4);
            return Boolean.TRUE;
        }
        if (qis.startWith("false")) {
            qis.skip(5);
            return Boolean.FALSE;
        }
        if (qis.startWith("undefined")) {
            qis.skip(9);
            return null;
        }
        if (qis.startWith("null")) {
            qis.skip(4);
            return null;
        }
        switch (qis.peek()) {
        case '.':
        case '0':
        case '1':
        case '2':
        case '3':
        case '4':
        case '5':
        case '6':
        case '7':
        case '8':
        case '9':
        case '-':
            // 看来是数字
            boolean hasPoint = qis.peek() == '.';
            sb.append((char) qis.poll());
            while (true) {
                if (qis.isEnd()) {// 读完了? 处理一下
                    if (hasPoint)
                        return Double.parseDouble(sb.toString());
                    else {
                        Long p = Long.parseLong(sb.toString());
                        if (Integer.MIN_VALUE < p.longValue() && p.longValue() < Integer.MAX_VALUE)
                            return p.intValue();
                        return p;
                    }
                }
                switch (qis.peek()) {
                case '0':
                case '1':
                case '2':
                case '3':
                case '4':
                case '5':
                case '6':
                case '7':
                case '8':
                case '9':
                    sb.append((char) qis.poll());
                    break;
                case '.':
                    if (hasPoint)
                        throw unexpectedChar();
                    else {
                        hasPoint = true;
                        sb.append((char) qis.poll());
                        break;
                    }
                case 'L':
                case 'l':
                    if (hasPoint)
                        throw unexpectedChar();
                    qis.poll();
                    return Long.parseLong(sb.toString());
                case 'F':
                case 'f':
                    qis.poll();
                    return Float.parseFloat(sb.toString());
                default: {// 越界读取了!!
                    if (hasPoint)
                        return Double.parseDouble(sb.toString());
                    else {
                        Long p = Long.parseLong(sb.toString());
                        if (Integer.MIN_VALUE < p.longValue() && p.longValue() < Integer.MAX_VALUE)
                            return p.intValue();
                        return p;
                    }
                }
                }
            }
        default:
            throw unexpectedChar();// 不是数值,不是布尔值,不是null和undefined? 玩野啊? 抛异常!!
        }
    }

    /**
     * 
     */
    private void parseMap(Map<String, Object> map) throws IOException {
        qis.poll();
        skipCommentsAndBlank();
        if (qis.peek() == '}') {
            qis.poll();
            return;
        }
        while (true) {
            parseMapItem(map);
            skipCommentsAndBlank();
            switch (qis.poll()) {
            case '}':
                return;
            case ',':
                skipCommentsAndBlank();
                // 如果结束
                if (qis.peek() == '}') {
                    qis.poll();
                    return;
                }
                continue;
            default:
                throw unexpectedChar();
            }
        }
    }

    /**
     * 生成MAP对象
     * 
     * @param map
     * @throws IOException
     */
    protected void parseMapItem(Map<String, Object> map) throws IOException {
        map.put(fetchKey(), parseFromHere());
    }

    /**
     * 找KEY
     */
    protected String fetchKey() throws IOException {
        // 找key
        String key = null;
        switch (qis.peek()) {
        case '"':
        case '\'':
            key = parseString();
            skipCommentsAndBlank();
            // 去掉":"
            if (!(qis.poll() == ':')) {
                throw makeError("Error JSON (KEY : VALUE) syntax!");
            }
            break;
        default:
            // 没办法,看来是无分隔符的字符串,找一下吧
            StringBuilder sb = new StringBuilder();
            // sb.append((char) qis.peek());
            OUTER: while (true) {
                switch (qis.peek()) {
                case '\\':// 特殊字符
                    qis.poll();
                    parseSp(sb);
                    break;
                case ' ':
                case '/':
                    qis.poll();
                    skipCommentsAndBlank();
                    if (qis.poll() == ':') {
                        key = sb.toString().trim().intern();
                        break OUTER;
                    } else
                        throw unexpectedChar();
                case ':':
                    qis.poll();
                    key = sb.toString().trim().intern();
                    break OUTER;
                default:
                    sb.append((char) qis.poll());
                }
            }
        }
        // TODO 判断一下key是否合法
        // 当前字符为: 跳过去
        skipCommentsAndBlank();
        return key;
    }

    /**
     * 处理List
     * 
     * @param list
     * @throws IOException
     */
    private void parseList(List<Object> list) throws IOException {
        qis.poll();
        skipCommentsAndBlank();
        if (qis.peek() == ']') {
            qis.poll();
            return;
        }
        while (true) {
            list.add(parseFromHere());
            skipCommentsAndBlank();
            switch (qis.poll()) {
            case ']':
                return;
            case ',':// 看来还有元素
                skipCommentsAndBlank();
                // 如果没有正确结束
                if (qis.peek() == ']') {
                    qis.poll();
                    return;
                }
                continue;
            default:
                throw unexpectedChar();
            }
        }

    }

    private void skipCommentsAndBlank() throws IOException {
        skipBlank();
        while (qis.peek() == '/') {
            int v = qis.peekNext();
            if (v == '/') { // inline comment
                qis.readLine();
            } else if (v == '*') { // block comment
                skipBlockComment();
            } else {
                throw makeError("Error comment syntax!");
            }
            skipBlank();
        }
    }

    private void skipBlank() throws IOException {
        while (qis.peek() >= 0 && qis.peek() <= 32)
            qis.poll();
    }

    private void skipBlockComment() throws IOException {
        // 过滤块注释开始的"/*"两个字符
        qis.skip(2);
        while (true) {
            if (qis.poll() == '*') {
                if (qis.peek() == '/') {
                    qis.poll();
                    break;
                }
            }
        }
    }

    private JsonException makeError(String message) throws IOException {
        return new JsonException(qis.getRow(), qis.getCol(), (char) qis.peek(), message);
    }

    private JsonException unexpectedChar() throws IOException {
        return new JsonException(qis.getRow(), qis.getCol(), (char) qis.peek(), "Unexpected char");
    }

    // public static void main(String[] args) {
    // StringReader sr = new
    // StringReader("{abc      :'ccc',ppp      : 123 ,                xx : true            }");
    // StringCompile2 sc2 = new StringCompile2();
    // System.out.println(sc2.parse(sr));
    // System.out.println(new StringCompile2().parse(new
    // StringReader("{abc:{abc:123f}}")));
    // System.out.println(new StringCompile2().parse(new
    // StringReader("{abc:{       abc:123f}}")));
    // System.out.println(new StringCompile2().parse(new
    // StringReader("{abc:{abc:      123f}}")));
    // System.out.println(new StringCompile2().parse(new
    // StringReader("[123,true]")));
    // System.out.println(new StringCompile2().parse(new
    // StringReader("[123,456]")));
    // System.out.println(new StringCompile2().parse(new
    // StringReader("[123,{abc:456}]")));
    // System.out.println(new StringCompile2().parse(new
    // StringReader("[123,456L]")));
    // System.out.println(new StringCompile2().parse(new
    // StringReader("123456789L")));
    // System.out.println(new StringCompile2().parse(new StringReader("2.3")));
    // System.out.println(new StringCompile2().parse(new StringReader("0.0f")));
    // System.out.println(new StringCompile2().parse(new
    // StringReader("2.9999")));
    // System.out.println(new StringCompile2().parse(new StringReader("true")));
    // System.out.println(new StringCompile2().parse(new
    // StringReader("false")));
    // System.out.println(new StringCompile2().parse(new StringReader("null")));
    // System.out.println(new StringCompile2().parse(new
    // StringReader("undefined")));
    // System.out.println(new StringCompile2().parse(new
    // StringReader("\"abc\"")));
    // System.out.println(new StringCompile2().parse(new
    // StringReader("\"a\'bc\"")));
    // System.out.println(new StringCompile2().parse(new
    // StringReader("\"\'a\\\"bc\"")));
    //
    // System.out.println(new StringCompile2().parse(new
    // StringReader("var ioc = {id:6};")));
    // }
}
