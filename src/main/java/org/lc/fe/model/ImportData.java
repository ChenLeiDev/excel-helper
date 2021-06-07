package org.lc.fe.model;

import java.util.ArrayList;
import java.util.List;

/**
 * @author 陈雷
 * @param <T>
 */
public final class ImportData<T> {
    private List<T> valid = new ArrayList<>();
    private List<DataNode<T>> invalid = new ArrayList<>();

    public List<T> getValid(){
        return valid;
    }

    public List<DataNode<T>> getInvalid(){
        return invalid;
    }

    public void addValid(T data){
        valid.add(data);
    }

    public void addInvalid(T data, InvalidInfo invalidInfo){
        invalid.add(new DataNode<>(invalidInfo, data));
    }
}
