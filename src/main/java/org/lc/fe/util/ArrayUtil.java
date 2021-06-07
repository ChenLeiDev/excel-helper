package org.lc.fe.util;

public class ArrayUtil {

    // ---------------------------------------------------------------------- isNotEmpty
    /**
     * 数组是否为非空
     * @param <T> 数组元素类型
     * @param array 数组
     * @return 是否为非空
     */
    public static <T> boolean isNotEmpty( T[] array) {
        return (array != null && array.length != 0);
    }

}
