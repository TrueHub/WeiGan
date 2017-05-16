package com.youyi.weigan.eventbean;

/**
 * Created by user on 2017/5/16.
 */

public class Comm2Frags {

    public enum Type{
        FromActivity , FromFragment;
    }

    private String instruct ;
    /**
     * type
     */
    private Type type ;

    public String getInstruct() {
        return instruct;
    }

    public void setInstruct(String instruct) {
        this.instruct = instruct;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public Comm2Frags(String instruct, Type type) {
        this.instruct = instruct;
        this.type = type;
    }
}
