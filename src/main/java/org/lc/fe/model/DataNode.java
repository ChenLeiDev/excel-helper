package org.lc.fe.model;
/**
 * @author 陈雷
 * @param <T>
 */
public class DataNode<T> {
    private InvalidInfo invalidInfo;
    private T data;

    protected DataNode(InvalidInfo invalidInfo, T data) {
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
