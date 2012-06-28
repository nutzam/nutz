package org.nutz.json;

@ToJson
public class Trout {

    public static enum COLOR {
        RED, BLUE, BLACK
    }

    private COLOR color;

    private float weight;

    public COLOR getColor() {
        return color;
    }

    public void setColor(COLOR color) {
        this.color = color;
    }

    public float getWeight() {
        return weight;
    }

    public void setWeight(float weight) {
        this.weight = weight;
    }

    public String toJson(JsonFormat format) {
        return Json.toJson(String.format("Trout[%s](%s)", color.name(), weight));
    }
}
