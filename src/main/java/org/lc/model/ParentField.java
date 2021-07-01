package org.lc.model;

import java.lang.reflect.Field;

public class ParentField {

    public Class subclass;
    public Field field;

    public ParentField(Class subclass, Field field){
        this.subclass = subclass;
        this.field = field;
    }
}
