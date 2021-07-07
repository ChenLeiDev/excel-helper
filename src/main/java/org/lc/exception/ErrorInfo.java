package org.lc.exception;

public enum ErrorInfo {

    COLUMN_DUPLICATE("列不唯一！"),
    FIELD_GENERIC_TYPE_MISSING("属性泛型缺失！"),
    FIELD_VALUE_MAPPING_ERROR("属性值映射错误！"),
    TEMPLATE_FILE_NOT_FOUND("未找到模板文件！");

    private String msg;

    ErrorInfo(String msg){
        this.msg = msg;
    }

    public String getMsg(){
        return msg;
    }
}
