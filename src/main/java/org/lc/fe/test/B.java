package org.lc.fe.test;

import org.lc.fe.annotation.ExcelColumn;
import org.lc.fe.annotation.ExcelDynamicModel;

import java.util.ArrayList;
import java.util.List;

public class B {
    @ExcelColumn(column = "爱好")
    private String hobby;



    @ExcelDynamicModel(subclass = ArrayList.class)
    private List<C> parents;
    public List<C> getParents() {
        return parents;
    }

    public void setParents(List<C> parents) {
        this.parents = parents;
    }
    public String getHobby() {
        return hobby;
    }

    public void setHobby(String hobby) {
        this.hobby = hobby;
    }

}
