package org.lc.fe;


import org.lc.fe.model.InvalidInfo;

public interface ExcelDataValidator<T> {

    void valid(T data, InvalidInfo invalidInfo);

}
