package org.lc.util.excel;

import com.alibaba.fastjson.JSON;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.lc.constant.Type;

import java.util.Date;

public class CellUtil {

    private CellUtil(){}

    public static void writeValue(XSSFRow row, Object value, Type type, int cellNum){
        XSSFCell cell = row.getCell(cellNum);
        if(cell == null){
            cell = row.createCell(cellNum);
        }
        cell.setCellType(type.getType());
        if(value == null){
            cell.setCellValue((String)null);
        }
        writeValue(type, cell, value);
    }

    public static void writeValue(Type type, XSSFCell cell, Object value){
        cell.setCellType(type.getType());
        if(value == null){
            return;
        }
        switch (type){
            case STRING:
                cell.setCellValue(String.valueOf(value));
                break;
            case NUMBER:
                cell.setCellValue(Double.valueOf(String.valueOf(value)));
                break;
            case DATE:
                cell.setCellValue((Date)value);
                break;
            case BOOLEAN:
                cell.setCellValue((Boolean) value);
                break;
            case FORMULA:
                cell.setCellValue((String)value);
                break;
        }
    }

    public static Object readValue(Type type, XSSFCell cell, Class valueType){
        CellType cellType = cell.getCellType();
        if(!cellType.equals(type.getType())){
            return null;
        }
        Object value = null;
        switch (type){
            case STRING:
                value = cell.getStringCellValue();
                if(String.class.equals(valueType)){
                    return value;
                }
                break;
            case NUMBER:
                value = cell.getNumericCellValue();
                break;
            case DATE:
                value = cell.getDateCellValue();
                if(Date.class.equals(valueType)){
                    return value;
                }
                break;
            case BOOLEAN:
                value = cell.getBooleanCellValue();
                break;
            case FORMULA:
                value = cell.getCellFormula();
                if(String.class.equals(valueType)){
                    return value;
                }
                break;
        }
        if(value == null){
            return null;
        }
        try {
            value = JSON.parseObject(String.valueOf(value), valueType);
        } catch (Exception e) {
            e.printStackTrace();
            value = null;
        }
        return value;
    }

}
