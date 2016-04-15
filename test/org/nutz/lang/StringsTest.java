package org.nutz.lang;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;

import org.junit.Test;

public class StringsTest {

    @Test
    public void test_split_with_escape_quote() {
        String[] list = Strings.split("a \"nm:\\\"A\\\"\"", false, ' ');
        assertEquals(2, list.length);
        assertEquals("a", list[0]);
        assertEquals("nm:\"A\"", list[1]);
    }

    @Test
    public void test_split_by_whitespace() {
        String[] list = Strings.split("  a   b   ", false, ' ');
        assertEquals(2, list.length);
        assertEquals("a", list[0]);
        assertEquals("b", list[1]);
    }

    @Test
    public void test_by_eacape() {
        String[] list = Strings.split("a\\nb | a\\ b", true, '|');
        assertEquals(2, list.length);
        assertEquals("anb ", list[0]);
        assertEquals(" a b", list[1]);
    }

    @Test
    public void test_by_escape_in_quote() {
        String[] list = Strings.split("me -set \"PS1=\\u:\\W$\"", true, '|');
        assertEquals(1, list.length);
        assertEquals("me -set \"PS1=\\u:\\W$\"", list[0]);
    }

    @Test
    public void test_pipes_parse2() {
        String[] list = Strings.split("a | b 'x' | d ", true, '|');
        assertEquals(3, list.length);
        assertEquals("a ", list[0]);
        assertEquals(" b 'x' ", list[1]);
        assertEquals(" d ", list[2]);
    }

    @Test
    public void test_pipes_parse() {
        String[] list = Strings.split(" a 'x' ", true, '|');
        assertEquals(1, list.length);
        assertEquals(" a 'x' ", list[0]);
    }

    @Test
    public void test_tokens_parse() {
        String[] list = Strings.split("echo 'a'bc", true, ' ');
        assertEquals(2, list.length);
        assertEquals("echo", list[0]);
        assertEquals("'a'bc", list[1]);

        list = Strings.split("echo 'a b'", false, ' ');
        assertEquals(2, list.length);
        assertEquals("echo", list[0]);
        assertEquals("a b", list[1]);
    }

    /**
     * for issue #606 (report by <a href="https://github.com/Rekoe">Rekoe</a>)
     */
    @Test
    public void test_isQuoteBy_null() {
        assertTrue(Strings.isQuoteBy("{abc}", "{", "}"));
        assertFalse(Strings.isQuoteBy(null, "{", "}"));
        assertFalse(Strings.isQuoteBy("{abc}", "{", null));
        assertFalse(Strings.isQuoteBy("{abc}", null, "}"));
        assertFalse(Strings.isQuoteBy("{abc}", null, null));
    }

    @Test
    public void test_is_full_width_character() {
        assertFalse(Strings.isFullWidthCharacter('a'));
        assertTrue(Strings.isFullWidthCharacter('ａ'));
        assertFalse(Strings.isFullWidthCharacter('a'));
        assertTrue(Strings.isFullWidthCharacter('ａ'));
        assertTrue(Strings.isFullWidthCharacter('。'));
    }

    @Test
    public void test_is_full_width_string() {
        assertTrue(Strings.isFullWidthString("おはようございます"));
        assertTrue(Strings.isFullWidthString("アイシダイク"));
        assertTrue(Strings.isFullWidthString("蒼井そら"));
        assertTrue(Strings.isFullWidthString("我是全角字我是办饺子"));
        assertTrue(Strings.isFullWidthString("ｗｏ我爱你中国，哈哈哈"));
        assertFalse(Strings.isFullWidthString("ｗｏ我爱你中国,哈哈哈"));
        assertFalse(Strings.isFullWidthString("23gdgrt34t98))*&%$#{}:~!@"));
    }

    @Test
    public void test_is_half_width_string() {
        assertFalse(Strings.isHalfWidthString("おはようございます"));
        assertFalse(Strings.isHalfWidthString("アイシダイク"));
        assertFalse(Strings.isHalfWidthString("蒼井そら"));
        assertFalse(Strings.isHalfWidthString("我是全角字我是办饺子"));
        assertFalse(Strings.isHalfWidthString("ｗｏ我爱你中国，哈哈哈"));
        assertFalse(Strings.isHalfWidthString("ｗｏ我爱你中国,哈哈哈"));
        assertTrue(Strings.isHalfWidthString("23gdgrt34t98))*&%$#{}:~!@"));
    }

    @Test
    public void test_is_chinese_character() {
        assertTrue(Strings.isChineseCharacter('你'));
        assertTrue(Strings.isChineseCharacter('．'));
        assertTrue(Strings.isChineseCharacter('。'));
        assertFalse(Strings.isChineseCharacter('A'));
        assertFalse(Strings.isChineseCharacter('3'));
        assertFalse(Strings.isChineseCharacter('.'));
        assertFalse(Strings.isChineseCharacter('#'));
        assertFalse(Strings.isChineseCharacter('~'));
        assertFalse(Strings.isChineseCharacter('`'));
    }

    @Test
    public void test_char_length() {
        assertEquals(5, Strings.charLength("12345"));
        assertEquals(10, Strings.charLength("１２３４５"));
        assertEquals(15, Strings.charLength("１２３４５12345"));
    }

    @Test
    public void test_dup_char_sequence() {
        assertEquals("", Strings.dup(null, 4));
        assertEquals("", Strings.dup("", 4));
        assertEquals("ssssssss", Strings.dup("ss", 4));
    }

    @Test
    public void test_dup_char() {
        assertEquals("", Strings.dup(' ', 0));
        assertEquals("aaaa", Strings.dup('a', 4));
        assertEquals("    ", Strings.dup(' ', 4));
    }

    @Test
    public void test_upperFirst() {
        assertNull(Strings.upperFirst(null));
        assertEquals("", Strings.upperFirst(""));
        assertEquals("A", Strings.upperFirst("a"));
        assertEquals("Aa", Strings.upperFirst("aa"));
        assertEquals("Aa", Strings.upperFirst("Aa"));
    }

    @Test
    public void test_lower_first() {
        assertNull(Strings.lowerFirst(null));
        assertEquals("", Strings.lowerFirst(""));
        assertEquals("aCV", Strings.lowerFirst("aCV"));
        assertEquals("eee", Strings.lowerFirst("eee"));
        assertEquals("vCD", Strings.lowerFirst("VCD"));
        assertEquals("vff", Strings.lowerFirst("Vff"));
    }

    @Test
    public void test_equals_string_ignore_case() {
        assertTrue(Strings.equalsIgnoreCase(null, null));
        assertFalse(Strings.equalsIgnoreCase(null, "a"));
        assertTrue(Strings.equalsIgnoreCase("aBc", "aBc"));
        assertTrue(Strings.equalsIgnoreCase("aBc", "abc"));
        assertTrue(Strings.equalsIgnoreCase("aBc", "abC"));
        assertFalse(Strings.equalsIgnoreCase("aB", "abC"));
    }

    @Test
    public void test_equals_string() {
        assertTrue(Strings.equals(null, null));
        assertFalse(Strings.equals(null, "a"));
        assertTrue(Strings.equalsIgnoreCase("aBc", "aBc"));
        assertFalse(Strings.equals("aBc", "abc"));
        assertFalse(Strings.equals("aBc", "abC"));
        assertFalse(Strings.equals("aB", "abC"));
    }

    @Test
    public void test_is_empty() {
        assertTrue(Strings.isEmpty(null));
        assertTrue(Strings.isEmpty(""));
        assertFalse(Strings.isEmpty("  "));
        assertFalse(Strings.isEmpty(" at "));
        assertFalse(Strings.isEmpty(new StringBuffer(" ")));
    }

    @Test
    public void test_is_blank() {
        assertTrue(Strings.isBlank(null));
        assertTrue(Strings.isBlank(""));
        assertTrue(Strings.isBlank("  "));
        assertFalse(Strings.isBlank(" at "));
        assertTrue(Strings.isBlank(new StringBuffer(" ")));
    }

    @Test
    public void test_trim() {
        assertEquals(null, Strings.trim(null));
        assertEquals("", Strings.trim(""));
        assertEquals("", Strings.trim(new StringBuffer("")));
        assertEquals("", Strings.trim(new StringBuffer(" ")));
        assertEquals("left", Strings.trim("left   "));
        assertEquals("right", Strings.trim("  right"));
        assertEquals("middle", Strings.trim(" middle   "));
        assertEquals("multi world", Strings.trim(" multi world "));
        assertEquals("nutz加油", Strings.trim(" nutz加油 "));
        assertEquals("", Strings.trim(new StringBuffer("    ")));
        assertEquals("multi world", Strings.trim(new StringBuffer("multi world")));
        assertEquals("multi world", Strings.trim(new StringBuffer(" multi world ")));
        assertEquals("nutz加油", Strings.trim(new StringBuilder(" nutz加油 ")));
    }

    @Test
    public void test_split_ignore_blank() {
        assertArrayEquals(null, Strings.splitIgnoreBlank(null));
        assertArrayEquals(new String[]{}, Strings.splitIgnoreBlank(" "));
        assertArrayEquals(new String[]{"2", "3", "5"}, Strings.splitIgnoreBlank("2,3,, 5"));
        assertArrayEquals(new String[]{"2", "3", "5", "6"}, Strings.splitIgnoreBlank("2,3,, 5,6,"));
        assertArrayEquals(new String[]{"2,3,5,6,"}, Strings.splitIgnoreBlank("2,3,5,6,", ",,"));
        assertArrayEquals(new String[]{"2,3", "5", "6,"},
                          Strings.splitIgnoreBlank("2,3 ,,5,,6,", ",,"));
        assertArrayEquals(new String[]{"2,3", "5", "6,"},
                          Strings.splitIgnoreBlank("2,3,,5 ,,,,6,", ",,"));
    }

    @Test
    public void test_fill_digit() {
        assertEquals("-1", Strings.fillDigit(-1, 2));
        assertEquals("0-1", Strings.fillDigit(-1, 3));
        assertEquals("1", Strings.fillDigit(1, -1));
        assertEquals("333", Strings.fillDigit(333, 2));
        assertEquals("0033", Strings.fillDigit(33, 4));
    }

    @Test
    public void test_fill_hex() {
        assertEquals("ffffffff", Strings.fillHex(-1, 2));
        assertEquals("1", Strings.fillHex(1, -1));
        assertEquals("14d", Strings.fillHex(333, 2));
        assertEquals("0021", Strings.fillHex(33, 4));
    }

    @Test
    public void test_fill_binary() {
        assertEquals("1", Strings.fillBinary(1, -1));
        assertEquals("100001", Strings.fillBinary(33, 2));
        assertEquals("00100001", Strings.fillBinary(33, 8));
    }

    @Test(expected = StringIndexOutOfBoundsException.class)
    public void test_to_digit() {
        assertEquals("1", Strings.toDigit(11, 1));
        assertEquals("011", Strings.toDigit(11, 3));
        assertEquals("", Strings.toDigit(1, 0));
        Strings.toDigit(1, -1);
    }

    @Test(expected = StringIndexOutOfBoundsException.class)
    public void test_to_hex() {
        assertEquals("b", Strings.toHex(11, 1));
        assertEquals("00b", Strings.toHex(11, 3));
        assertEquals("", Strings.toHex(11, 0));
        Strings.toHex(1, -1);
    }

    @Test(expected = StringIndexOutOfBoundsException.class)
    public void test_to_binary() {
        assertEquals("1", Strings.toBinary(11, 1));
        assertEquals("011", Strings.toBinary(11, 3));
        assertEquals("", Strings.toBinary(11, 0));
        Strings.toBinary(1, -1);
    }

    @Test(expected = StringIndexOutOfBoundsException.class)
    public void test_cut_right() {
        assertNull(Strings.cutRight(null, 2, 'c'));
        assertEquals("ca", Strings.cutRight("a", 2, 'c'));
        assertEquals("ab", Strings.cutRight("ab", 2, 'c'));
        assertEquals("bc", Strings.cutRight("abc", 2, 'c'));
        assertEquals("", Strings.cutRight("abc", 0, 'c'));
        Strings.cutRight("abc", -1, 'c');
    }

    @Test(expected = StringIndexOutOfBoundsException.class)
    public void test_cut_left() {
        assertNull(Strings.cutLeft(null, 2, 'c'));
        assertEquals("ac", Strings.cutLeft("a", 2, 'c'));
        assertEquals("ab", Strings.cutLeft("ab", 2, 'c'));
        assertEquals("ab", Strings.cutLeft("abc", 2, 'c'));
        assertEquals("", Strings.cutLeft("abc", 0, 'c'));
        Strings.cutLeft("abc", -1, 'c');
    }

    @Test
    public void test_align_left() {
        assertNull(Strings.alignLeft(null, 2, 'c'));
        assertEquals("cc", Strings.alignLeft("", 2, 'c'));
        assertEquals("acc", Strings.alignLeft("a", 3, 'c'));
        assertEquals("aaaa", Strings.alignLeft("aaaa", 3, 'c'));
    }

    @Test
    public void test_align_right() {
        assertNull(Strings.alignRight(null, 2, 'c'));
        assertEquals("cc", Strings.alignRight("", 2, 'c'));
        assertEquals("cca", Strings.alignRight("a", 3, 'c'));
        assertEquals("aaaa", Strings.alignRight("aaaa", 3, 'c'));
    }

    @Test
    public void test_is_quote_by_ignore_blank() {
        assertTrue(Strings.isQuoteByIgnoreBlank("[AB]", '[', ']'));
        assertTrue(Strings.isQuoteByIgnoreBlank("[]", '[', ']'));
        assertTrue(Strings.isQuoteByIgnoreBlank("   []", '[', ']'));
        assertTrue(Strings.isQuoteByIgnoreBlank("[]   ", '[', ']'));
        assertTrue(Strings.isQuoteByIgnoreBlank("  [  AB  ]   ", '[', ']'));
        assertTrue(Strings.isQuoteByIgnoreBlank("  [  AB  ]", '[', ']'));
        assertTrue(Strings.isQuoteByIgnoreBlank("[  AB  ]   ", '[', ']'));
        assertFalse(Strings.isQuoteByIgnoreBlank(null, '[', ']'));
        assertFalse(Strings.isQuoteByIgnoreBlank("", '[', ']'));
        assertFalse(Strings.isQuoteByIgnoreBlank("[AB", '[', ']'));
        assertFalse(Strings.isQuoteByIgnoreBlank("   [AB", '[', ']'));
        assertFalse(Strings.isQuoteByIgnoreBlank("AB]", '[', ']'));
        assertFalse(Strings.isQuoteByIgnoreBlank("AB]   ", '[', ']'));
    }

    @Test
    public void test_is_quote_by() {
        assertTrue(Strings.isQuoteBy("[AB]", '[', ']'));
        assertFalse(Strings.isQuoteBy(null, '[', ']'));
        assertFalse(Strings.isQuoteBy("   ", '[', ']'));
        assertFalse(Strings.isQuoteBy("", '[', ']'));
        assertFalse(Strings.isQuoteBy("[", '[', ']'));
        assertFalse(Strings.isQuoteBy("[AB", '[', ']'));
        assertFalse(Strings.isQuoteBy("AB]", '[', ']'));
        assertFalse(Strings.isQuoteBy("  [AB]  ", '[', ']'));
    }

    @Test
    public void test_max_length_4_collection() {
        assertEquals(0, Strings.maxLength((Collection<String>) null));
        assertEquals(0, Strings.maxLength(new HashSet<CharSequence>()));
        assertEquals(3, Strings.maxLength(Arrays.asList("a", "bb", "ccc")));
    }

    @Test
    public void test_max_length_4_array() {
        assertEquals(0, Strings.maxLength((String[]) null));
        assertEquals(0, Strings.maxLength(new String[4]));
        assertEquals(3, Strings.maxLength(new String[]{"a", "bb", "ccc"}));
    }

    @Test
    public void test_default_string_when_null() {
        assertEquals("", Strings.sNull(null));
        assertEquals("", Strings.sNull(null, ""));
        assertEquals(" ", Strings.sNull(" ", "b"));
        assertEquals("a", Strings.sNull("a", "b"));
    }

    @Test
    public void test_default_string_when_blank() {
        assertEquals("", Strings.sBlank(null));
        assertEquals("", Strings.sBlank(null, ""));
        assertEquals("b", Strings.sBlank(" ", "b"));
        assertEquals("a", Strings.sBlank("a", "b"));
    }

    @Test
    public void test_remove_first() {
        assertNull(Strings.removeFirst(null));
        assertEquals("2345", Strings.removeFirst("12345"));
        assertEquals("", Strings.removeFirst(""));
        assertEquals("", Strings.removeFirst("A"));
    }

    @Test
    public void test_remove_first_special_char() {
        assertNull(Strings.removeFirst(null, 'A'));
        assertEquals("BCD", Strings.removeFirst("ABCD", 'A'));
        assertEquals("", Strings.removeFirst("", 'A'));
        assertEquals("", Strings.removeFirst("A", 'A'));
        assertEquals("ABCD", Strings.removeFirst("ABCD", 'B'));
    }

    @Test
    public void test_string_is_in_array() {
        String[] arr = {"a", "bb", null, "ccc"};
        assertFalse(Strings.isin(arr, null));
        assertTrue(Strings.isin(arr, "a"));
        assertFalse(Strings.isin(arr, "ccC"));
    }

    @Test
    public void test_is_email() {
        assertFalse(Strings.isEmail(""));
        assertFalse(Strings.isEmail("mc02cxj"));
        assertFalse(Strings.isEmail("mc02cxj@gmail"));
        assertTrue(Strings.isEmail("mc02cxj@gmail.com"));
        assertTrue(Strings.isEmail("mc02cxj@sina.com.cn"));
        assertTrue(Strings.isEmail("mc02cxj.test@sina.com.cn"));
        Strings.isEmail(null);
    }

    @Test
    public void test_upper_word() {
        assertEquals("", Strings.upperWord("-", '-'));
        assertEquals("", Strings.upperWord("---", '-'));
        assertEquals("aBCD", Strings.upperWord("a-b-c-d", '-'));
        assertEquals("helloWorld", Strings.upperWord("hello-world", '-'));
    }

    @Test
    public void test_escape_html() {
        assertEquals("&lt;/article&gt;Oops &lt;script&gt;alert(&quot;hello world&quot;);&lt;/script&gt;",
                     Strings.escapeHtml("</article>Oops <script>alert(\"hello world\");</script>"));
        assertEquals("alert(&#x27;hello world&#x27;);",
                     Strings.escapeHtml("alert('hello world');"));
    }

    @Test
    public void test_issue461() {
        char here_is_zenkaku_space_char = '　';
        String here_is_zenkaku_space_str = String.valueOf(here_is_zenkaku_space_char);
        StringBuffer here_is_zenkaku_space_sb = new StringBuffer(here_is_zenkaku_space_str);
        StringBuilder here_is_zenkaku_space_sber = new StringBuilder(here_is_zenkaku_space_str);

        assertEquals("aaa",
                     Strings.trim(here_is_zenkaku_space_str + "aaa" + here_is_zenkaku_space_str));
        assertEquals("aaa",
                     Strings.trim(here_is_zenkaku_space_sb.append("aaa")
                                                          .append(here_is_zenkaku_space_char)));
        assertEquals("aaa",
                     Strings.trim(here_is_zenkaku_space_sber.append("aaa")
                                                            .append(here_is_zenkaku_space_char)));
    }

    @Test
    public void test_join_array() throws Exception {
        assertTrue("1920x1080".equals(Strings.join("x", new String[]{"1920", "1080"})));
        assertTrue("1920x1080".equals(Strings.join("x", new Integer[]{1920, 1080})));
    }

    @Test
    public void test_change_charset() throws Exception {
        assertTrue("你妹的".equals(Strings.changeCharset("\u4f60\u59b9\u7684",
                                                      Encoding.CHARSET_UTF8)));
        assertTrue("nutz是个好类库".equals(Strings.changeCharset("nutz\u662f\u4e2a\u597d\u7c7b\u5e93",
                                                            Encoding.CHARSET_UTF8)));
    }
}
