package org.lc;


import org.lc.model.InvalidInfo;

public interface ExcelDataValidator<T> {

    void valid(T data, InvalidInfo invalidInfo);

}
