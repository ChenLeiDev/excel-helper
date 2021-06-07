package org.lc.fe.constant;

import org.apache.poi.ss.usermodel.CellType;

public enum Type {

    STRING(CellType.STRING),

    NUMBER(CellType.NUMERIC),

    DATE(CellType.NUMERIC),

    BOOLEAN(CellType.BOOLEAN),

    FORMULA(CellType.FORMULA);

    private CellType type;

    Type(CellType type){
        this.type = type;
    }

    public CellType getType(){
        return type;
    }
}
