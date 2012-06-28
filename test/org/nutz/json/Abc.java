package org.nutz.json;

public class Abc {
    public Abc() {}

    public Abc(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int id;
    public String name;

    @Override
    public int hashCode() {
        return null == name ? 0 : name.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (super.equals(obj))
            return true;
        if (id != ((Abc) obj).id)
            return false;
        if (!name.equals(((Abc) obj).name))
            return false;
        return true;
    }

}
