package org.nutz.json.meta;

import java.util.List;

import org.nutz.json.JsonField;
import org.nutz.lang.util.IntRegion;

/**
 * 测试一下强制某字段输出 String
 * 
 * @author zozoh(zozohtnt@gmail.com)
 */
public class JX {

    private int n;

    @JsonField(forceString = true)
    private IntRegion region;

    @JsonField(forceString = true)
    private IntRegion[] regionArray;

    @JsonField(forceString = true)
    private List<IntRegion> regionList;

    public int getN() {
        return n;
    }

    public void setN(int n) {
        this.n = n;
    }

    public IntRegion getRegion() {
        return region;
    }

    public void setRegion(IntRegion region) {
        this.region = region;
    }

    public IntRegion[] getRegionArray() {
        return regionArray;
    }

    public void setRegionArray(IntRegion[] regionArray) {
        this.regionArray = regionArray;
    }

    public List<IntRegion> getRegionList() {
        return regionList;
    }

    public void setRegionList(List<IntRegion> regionList) {
        this.regionList = regionList;
    }

}
