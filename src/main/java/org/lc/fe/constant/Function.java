package org.lc.fe.constant;

import org.lc.fe.util.excel.ExcelData;

public enum Function {
    SUM("SUM", false, "合计"),//所有数字和
    SUMIF("SUMIF", true, "合计"),

    AVERAGE("AVERAGE", false, "平均"),//所有非空且数字的平均值
    AVERAGEA("AVERAGEA", false, "平均"),//false算0，true算1，字符串算0
    AVERAGEIF("AVERAGEIF", true, "平均"),

    MAX("MAX", false, "最大"),//所有非空数字的最大值
    MAXA("MAXA", false, "最大"),//数字以外的都算0

    MIN("MIN", false, "最小"),//所有非空数字的最小值
    MINA("MINA", false, "最小"),//数字以外的都算0

    COUNT("COUNT", false, "计数"),//所有格子数量
    COUNTA("COUNTA", false, "计数"),//非空单元格数量
    COUNTIF("COUNTIF", true, "计数");

    private final String function;
    private final boolean hasIf;
    private final String describle;

    Function(String function, boolean hasIf, String describle) {
        this.function = function;
        this.hasIf = hasIf;
        this.describle = describle;
    }

    public String getFunction(){
        return function;
    }

    public boolean getHasIf(){
        return hasIf;
    }

    public String getDescrible(){
        return describle;
    }

    public String getFormula(int col, int startRow, int endRow, String condition){
        StringBuilder stringBuilder = new StringBuilder(32)
                .append("\"")
                .append(this.describle)
                .append("：\"&")
                .append(this.function)
                .append("(")
                .append(ExcelData.getColumnName(col, startRow))
                .append(":")
                .append(ExcelData.getColumnName(col, endRow));

        if(hasIf){
            stringBuilder.append(",\"")
                    .append(condition)
                    .append("\"");
        }
        stringBuilder.append(")");
        return stringBuilder.toString();
    }

}
