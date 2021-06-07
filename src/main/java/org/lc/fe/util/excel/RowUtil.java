package org.lc.fe.util.excel;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.lc.fe.constant.Type;
import org.lc.fe.util.StringUtil;
import org.lc.fe.model.UnitElement;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class RowUtil {

    private RowUtil(){}

    public static void writeHeaders(XSSFRow row, Collection<String> values, int startCell){
        Iterator<Cell> cellIterator = row.cellIterator();
        Set<String> distinct = new HashSet<>();
        while(cellIterator.hasNext()){
            Cell cell = cellIterator.next();
            try{
                String header = cell.getStringCellValue();
                if(StringUtil.isNotBlank(header)){
                    distinct.add(header);
                }
            }catch (IllegalStateException e){
                e.printStackTrace();
            }
        }
        for (String value: values) {
            if(!UnitElement.HAS_FUNCTION.equals(value) && distinct.add(value)){
                CellUtil.writeValue(row, value, Type.STRING, startCell++);
            }
        }
    }

}
