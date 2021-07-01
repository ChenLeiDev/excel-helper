package org.lc.test;

import org.lc.annotation.ExcelColumn;

public class C {
    @ExcelColumn(column = "称谓")
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
