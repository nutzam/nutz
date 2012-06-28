package org.nutz.json.meta;

import org.nutz.json.ToJson;

@ToJson
class NoPublicClass {

    String name = "Wendal";
    
    public String getName() {
        throw new RuntimeException();
    }
    
    public String toJson() {
        return "ItMe";
    }
}
