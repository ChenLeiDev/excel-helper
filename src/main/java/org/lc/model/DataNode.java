package org.lc.model;
/**
 * @author 陈雷
 * @param <T>
 */
public class DataNode<T> {
    private InvalidInfo invalidInfo;
    private T data;

    public DataNode(InvalidInfo invalidInfo, T data) {
        this.invalidInfo = invalidInfo;
        this.data = data;
    }

    public InvalidInfo getInvalidInfo(){
        return invalidInfo;
    }

    public T getData(){
        return data;
    }
}
