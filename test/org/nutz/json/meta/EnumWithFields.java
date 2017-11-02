package org.nutz.json.meta;

import org.nutz.json.JsonShape;
import org.nutz.json.JsonShape.Type;

@JsonShape(Type.OBJECT)
public enum EnumWithFields {
    
    STAY_PUSH("1600","待推单"),
    PART_PUSHED("1601","部分推单"),
    PUSHED("1602","推单完成");

    private String code;
    private String description;
    
    EnumWithFields(String code, String description) {
        this.code = code;
        this.description = description;
    }
}
