package org.lc.util;

import java.util.Objects;

public class EnvironmentUtil {

    private EnvironmentUtil(){}

    /**
     * Class 是否从 ide 启动
     *
     * @param clazz 某 Class
     * @param <T>   泛型
     * @return boolean
     * @author wangtan
     * @date 2021-02-26 10:34:44
     * @since 1.0.0
     */
    public static <T> boolean isStartupFromFile(Class<T> clazz) {
        String protocol = clazz.getResource("").getProtocol();
        return Objects.equals(protocol, "file");
    }

    /**
     * Class 是否从 jar 包启动
     *
     * @param clazz 某 Class
     * @param <T>   泛型
     * @return boolean
     * @author wangtan
     * @date 2021-02-26 10:29:56
     * @since 1.0.0
     */
    public static <T> boolean isStartupFromJar(Class<T> clazz) {
        String protocol = clazz.getResource("").getProtocol();
        return Objects.equals(protocol, "jar");
    }
}
