package org.nutz.lang;

public interface TypeExtractor {

	Class<?>[] extract(Mirror<?> mirror);

}
