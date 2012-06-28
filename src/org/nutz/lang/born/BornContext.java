package org.nutz.lang.born;

import org.nutz.lang.MatchType;

public class BornContext<T> {

    private Borning<T> borning;

    private Object[] args;

    private MatchType matchType;

    private Object lackArg;

    private Class<?>[] castType;

    public Borning<T> getBorning() {
        return borning;
    }

    public BornContext<T> setBorning(Borning<T> borning) {
        this.borning = borning;
        return this;
    }

    public Object[] getArgs() {
        return args;
    }

    public BornContext<T> setArgs(Object[] args) {
        this.args = args;
        return this;
    }

    public MatchType getMatchType() {
        return matchType;
    }

    public void setMatchType(MatchType matchType) {
        this.matchType = matchType;
    }

    public Object getLackArg() {
        return lackArg;
    }

    public void setLackArg(Object lackArg) {
        this.lackArg = lackArg;
    }

    public Class<?>[] getCastType() {
        return castType;
    }

    public void setCastType(Class<?>[] castType) {
        this.castType = castType;
    }

    public T doBorn() {
        return borning.born(args);
    }

}
