package org.nutz.json;

import java.util.List;

import org.nutz.json.entity.JsonEntityField;
import org.nutz.lang.Mirror;

/**
 * JsonEntityFieldMaker
 * 通过定制JsonEntityField的生成过程来影响toJson的行为
 *
 * @author 幸福的旁边(happyday517@163.com)
 */
public interface JsonEntityFieldMaker {
    List<JsonEntityField> make(Mirror<?> mirror);
}