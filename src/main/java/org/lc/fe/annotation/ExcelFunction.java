package org.lc.fe.annotation;

import org.lc.fe.constant.Condition;
import org.lc.fe.constant.Function;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author 陈雷
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ExcelFunction {
    Function function();
    String condition() default Condition.NOT_BLANK;
}
