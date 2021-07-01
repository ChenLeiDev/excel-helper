package org.lc.annotation;

import org.lc.constant.AnnotationConstants;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ExcelModel {
    String template();//模板文件名
    String sheet();//模板文件中sheet页
    int header() default AnnotationConstants.DEFAULT_HEADER_ROW_NUM;//模板中表头行数，默认0
    int start() default AnnotationConstants.DEFAULT_DATA_START_ROW_NUM;//数据表中数据开始行数，默认表头行数 +1
}
