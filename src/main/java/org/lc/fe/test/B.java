package org.lc.fe.test;

import org.lc.fe.annotation.ExcelColumn;
import org.lc.fe.annotation.ExcelDynamicModel;

import java.util.ArrayList;
import java.util.List;

public class B {
    @ExcelColumn(column = "爱好")
    private String hobby;

    @ExcelColumn(column = "爱好id")
    private Long id;



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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
