package org.lc.fe.exception;

public enum ErrorInfo {

    COLUMN_DUPLICATE_ERROR("列名重复！"),
    FIELD_VALUE_MAPPING_ERROR("属性值映射错误！");

    private String msg;

    ErrorInfo(String msg){
        this.msg = msg;
    }

    public String getMsg(){
        return msg;
    }
}
