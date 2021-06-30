package org.lc.fe.exception;

public enum ErrorInfo {

    COLUMN_DUPLICATE("列不唯一！"),
    FIELD_GENERIC_TYPE_MISSING("属性泛型缺失！"),
    FIELD_VALUE_MAPPING_ERROR("属性值映射错误！");

    private String msg;

    ErrorInfo(String msg){
        this.msg = msg;
    }

    public String getMsg(){
        return msg;
    }
}
