package org.nutz.lang.util;

import java.util.regex.Pattern;

import org.nutz.repo.cache.simple.LRUCache;

public class Regex {

    protected static LRUCache<String, Pattern> cache = new LRUCache<String, Pattern>(10000);
    
    public static void setCacheSize(int size) {
        cache.setCacheSize(size);
    }
    
    public static void clear() {
        if (cache != null)
            cache.clear();
    }
    
    public static Pattern getPattern(String regex) {
        Pattern pattern = cache.get(regex);
        if (pattern == null) {
            pattern = Pattern.compile(regex);
            cache.put(regex, pattern);
        }
        return pattern;
    }
    
    public static boolean match(String regex, String value) {
        return getPattern(regex).matcher(value).find();
    }
}
