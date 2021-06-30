package org.lc.fe.util.excel;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.usermodel.*;
import org.lc.fe.constant.Type;
import org.lc.fe.exception.ErrorInfo;
import org.lc.fe.exception.XlsxParseException;
import org.lc.fe.model.*;
import org.lc.fe.util.ArrayUtil;
import org.lc.fe.util.CollectionUtil;
import org.lc.fe.util.StringUtil;
import org.lc.fe.ExcelDataValidator;
import org.lc.fe.ExcelHelper;
import org.lc.fe.ExcelHelperConfiguration;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class ExcelData {

    private static final CellCopyPolicy CELL_COPY_POLICY = new CellCopyPolicy();

    private ExcelData(){}

    private static final Map<String, Class> CLASS_FOR_NAME = new ConcurrentHashMap<>();

    public static void setPulls(int dataSize, ClassAndTemplateInfo classAndTemplateInfo){
        int endRow = dataSize + classAndTemplateInfo.startRowNum;//无数据则填充预留的一行
        if(dataSize > 0){
            endRow-=1;//有数据则填充所有数据覆盖的行
        }
        for(int col = 0; col < classAndTemplateInfo.headerCells.length; col++){//设置下拉框
            UnitElement unitElement = classAndTemplateInfo.unitElements.get(classAndTemplateInfo.headerCells[col]);
            if(unitElement != null){
                if(ArrayUtil.isNotEmpty(unitElement.pulls)){
                    int totalLength = 0;
                    for(String pull: unitElement.pulls){
                        totalLength += pull.length();
                    }
                    DataValidationHelper helper = classAndTemplateInfo.xssfSheet.getDataValidationHelper();
                    DataValidationConstraint constraint = null;
                    if(totalLength > 85){//utf-8下汉字长度超过85则使用隐藏sheet实现下拉框
                        String hidddenSheetName = ExcelHelper.HIDDEN_PULLS_SHEET_PREFIX.concat(unitElement.pullsFlag.toUpperCase());
                        Sheet hidden = classAndTemplateInfo.xssfWorkbook.createSheet(hidddenSheetName);
                        Cell cell = null;
                        for (int i = 0, length = unitElement.pulls.length; i < length; i++) {
                            Row row = hidden.createRow(i);
                            cell = row.createCell(0);
                            cell.setCellValue(unitElement.pulls[i]);
                        }
                        Name namedCell = classAndTemplateInfo.xssfWorkbook.createName();
                        namedCell.setNameName(hidddenSheetName);
                        namedCell.setRefersToFormula(hidddenSheetName.concat("!$A$1:$A$").concat(String.valueOf(unitElement.pulls.length)));
                        //sheet设置为隐藏
                        classAndTemplateInfo.xssfWorkbook.setSheetHidden(classAndTemplateInfo.xssfWorkbook.getSheetIndex(hidden), true);
                        //加载数据,将名称为hidden的
                        constraint = helper.createFormulaListConstraint(hidddenSheetName);
                    }else{
                        constraint = helper.createExplicitListConstraint(unitElement.pulls);
                    }
                    // 设置数据有效性加载在哪个单元格上,四个参数分别是：起始行、终止行、起始列、终止列
                    CellRangeAddressList addressList = new CellRangeAddressList(classAndTemplateInfo.startRowNum, endRow, col, col);
                    // 创建 DataValidation
                    DataValidation validation = helper.createValidation(constraint, addressList);
                    validation .setSuppressDropDownArrow(true);
                    validation .setShowErrorBox(true);
                    classAndTemplateInfo.xssfSheet.addValidationData(validation);
                }
            }
        }
    }

    public static void setRowStyle(int dataSize, ClassAndTemplateInfo classAndTemplateInfo) {
        classAndTemplateInfo.endRowNum = dataSize + classAndTemplateInfo.startRowNum;
        XSSFRow baseRow = classAndTemplateInfo.xssfSheet.getRow(classAndTemplateInfo.startRowNum);
        if(baseRow == null){
            baseRow = classAndTemplateInfo.xssfSheet.createRow(classAndTemplateInfo.startRowNum);
        }
        XSSFDataFormat dataFormat = classAndTemplateInfo.xssfWorkbook.createDataFormat();
        for (int col = 0; col < classAndTemplateInfo.headerCells.length; col++){
            if(StringUtil.isNotBlank(classAndTemplateInfo.headerCells[col])){
                UnitElement unitElement = classAndTemplateInfo.unitElements.get(classAndTemplateInfo.headerCells[col]);
                if(unitElement != null){
                    if(unitElement.format != null){
                        XSSFCell cell = baseRow.getCell(col);
                        if(cell == null){
                            cell = baseRow.createCell(col);
                        }
                        XSSFCellStyle cellStyle = cell.getCellStyle();
                        if(cellStyle == null){
                            cellStyle = classAndTemplateInfo.xssfWorkbook.createCellStyle();
                        }
                        cellStyle.setDataFormat(dataFormat.getFormat(unitElement.format));
                    }
                }
            }
        }
        for(int i = classAndTemplateInfo.startRowNum + 1; i < classAndTemplateInfo.endRowNum; i++){
            classAndTemplateInfo.xssfSheet.copyRows(classAndTemplateInfo.startRowNum, classAndTemplateInfo.startRowNum, i, CELL_COPY_POLICY);
        }
    }

    public static void writeDataTask(List data, ClassAndTemplateInfo classAndTemplateInfo) throws XlsxParseException {
        if(!CollectionUtil.isNotEmpty(data)){
            return;
        }
        boolean runAsync = ExcelHelperConfiguration.getRunAsync();
        if(runAsync){
            int pageLimit = ExcelHelperConfiguration.getPageLimit();
            AtomicInteger finished = new AtomicInteger(0);
            int totalTask = data.size() / pageLimit;
            if(data.size() % pageLimit > 0){
                totalTask++;
            }
            AtomicBoolean exc = new AtomicBoolean(false);
            for (int step = 0; ; step += pageLimit){
                int localStart = step;
                int localEnd = step + pageLimit < data.size() ? step + pageLimit : data.size();
                if(localStart < localEnd){
                    List subList = data.subList(localStart, localEnd);
                    CompletableFuture.runAsync(()->{
                        try {
                            writeData(subList, classAndTemplateInfo, classAndTemplateInfo.startRowNum + localStart);
                        } catch (IllegalAccessException e) {
                            exc.getAndSet(true);
                        } catch (Exception e){
                            e.printStackTrace();
                        } finally {
                            finished.incrementAndGet();
                        }
                    }, ExcelHelperConfiguration.getAsyncTaskPool());
                }else{
                    break;
                }
            }
            while(true){
                if(exc.get()){
                    throw new XlsxParseException(ErrorInfo.FIELD_VALUE_MAPPING_ERROR.getMsg());
                }
                if(finished.intValue() == totalTask){
                    break;
                }
            }
        }else {
            try{
                writeData(data, classAndTemplateInfo, classAndTemplateInfo.startRowNum);
            } catch (IllegalAccessException e) {
                throw new XlsxParseException(ErrorInfo.FIELD_VALUE_MAPPING_ERROR.getMsg());
            }
        }
        dealFunction(classAndTemplateInfo);
    }

    private static void dealFunction(ClassAndTemplateInfo classAndTemplateInfo){
        if(classAndTemplateInfo.unitElements.get(UnitElement.HAS_FUNCTION) == null){//求底行和
            return;
        }
        int endRowNum = classAndTemplateInfo.endRowNum;
        if(classAndTemplateInfo.startRowNum > endRowNum){
            endRowNum++;
        }
        XSSFRow lastRow = classAndTemplateInfo.xssfSheet.getRow(endRowNum);
        if(lastRow == null){
            lastRow = classAndTemplateInfo.xssfSheet.createRow(endRowNum);
        }
        for(int col = 0; col < classAndTemplateInfo.headerCells.length; col++){
            String headerCell = classAndTemplateInfo.headerCells[col];
            if(headerCell == null){
                continue;
            }
            UnitElement unitElement = classAndTemplateInfo.unitElements.get(headerCell);
            if(unitElement == null){
                continue;
            }
            XSSFCell cell = lastRow.getCell(col);
            if(cell == null){
                cell = lastRow.createCell(col);
            }
            XSSFCellStyle cellStyle = classAndTemplateInfo.xssfWorkbook.createCellStyle();
            if(lastRow == null || lastRow.getCell(col) == null){
                cellStyle.setAlignment(HorizontalAlignment.CENTER); //水平居中
                cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);   // 垂直居中
            }else {
                XSSFCell lastRowCell = lastRow.getCell(col);
                if(lastRowCell == null){
                    cellStyle.setAlignment(HorizontalAlignment.CENTER); //水平居中
                    cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);   // 垂直居中
                }else {
                    XSSFCellStyle lastRowCellStyle = lastRowCell.getCellStyle();
                    cellStyle.setAlignment(lastRowCellStyle.getAlignmentEnum());
                    cellStyle.setVerticalAlignment(lastRowCellStyle.getVerticalAlignmentEnum());
                }
            }
            cell.setCellStyle(cellStyle);
            if(unitElement.function != null){
                String formula = unitElement.function.getFormula(col, classAndTemplateInfo.startRowNum, endRowNum - 1, unitElement.condition);
                cell.setCellFormula(formula);
            }else{
                cell.setCellValue("--");
            }
        }
    }

    public static void writeData(List data, ClassAndTemplateInfo classAndTemplateInfo, int startRow) throws IllegalAccessException {
        for (int index = 0; index < data.size(); index++) {
            Object dataElement = data.get(index);
            if(dataElement == null){
                startRow++;
                continue;
            }
            XSSFRow row = classAndTemplateInfo.xssfSheet.getRow(startRow);
            Map<Object, Integer> compute = new HashMap<>(0);
            for(int col = 0; col < classAndTemplateInfo.headerCells.length; col++){
                String headerCell = classAndTemplateInfo.headerCells[col];
                if(headerCell == null){
                    continue;
                }
                UnitElement unitElement = classAndTemplateInfo.unitElements.get(headerCell);
                if(unitElement == null){
                    continue;
                }
                XSSFCell cell = row.getCell(col);
                if(cell == null){
                    cell = row.createCell(col);
                }
                Object value = null;
                if(dataElement.getClass().equals(DataNode.class)){
                    value = getFieldValue(unitElement, ((DataNode)dataElement).getData(), compute, headerCell);
                    String comment = ((DataNode)dataElement).getInvalidInfo().getInvalid(unitElement.field.getName());
                    if(comment != null){
                        XSSFComment cellComment = classAndTemplateInfo.drawingPatriarch.createCellComment(new XSSFClientAnchor(0, 0, 0, 0, (short) 3, 3, (short) 5, 6));
                        cellComment.setString(comment);
                        cell.setCellComment(cellComment);
                    }
                }else{
                    value = getFieldValue(unitElement, dataElement, compute, headerCell);
                }
                Type type = unitElement.type;
                if(unitElement.pulls != null){
                    if(value != null){
                        String pullValue = classAndTemplateInfo.pullNodes.getOutValue(unitElement.pullsFlag, value);
                        if(pullValue != null){
                            value = pullValue;
                        }
                    }
                    type = Type.STRING;
                }
                CellUtil.writeValue(type, cell, value);
            }
            startRow++;
        }
    }

    private static Object getFieldValue(UnitElement unitElement, Object dataElement, Map<Object, Integer> compute, String header) throws IllegalAccessException {
        List<ParentField> parents = unitElement.parents;
        if(parents.isEmpty()){
            return unitElement.field.get(dataElement);
        }
        for (int index = 0; index < parents.size(); index++){
            dataElement = parents.get(index).field.get(dataElement);
            if(dataElement == null){
                return null;
            }
            if(dataElement instanceof List){
                Object computeKey = unitElement;
                List listDataElement = (List)dataElement;
                Integer computeNum = compute.get(computeKey);
                if(computeNum == null){
                    computeNum = 0;
                }
                if(computeNum < listDataElement.size()){
                    dataElement = listDataElement.get(computeNum);
                    if(index == parents.size() - 1){
                        compute.put(computeKey, computeNum + 1);
                        return unitElement.field.get(dataElement);
                    }
                }else{
                    return null;
                }
            }else{
                if(index == parents.size() - 1){
                    return unitElement.field.get(dataElement);
                }
            }
        }
        return null;
    }

    public static String getColumnName(int col, int row){
        StringBuilder stringBuilder = new StringBuilder(3)
                .append(CellReference.convertNumToColString(col))
                .append(row + 1);
        return stringBuilder.toString();
    }

    public static <T> ImportData<T> readData(Class<T> clazz, ClassAndTemplateInfo classAndTemplateInfo) throws XlsxParseException {
        ImportData<T> importData= new ImportData<>();
        int lastRowNum = classAndTemplateInfo.xssfSheet.getLastRowNum();
        if(lastRowNum <= classAndTemplateInfo.headerRowNum){
            return importData;
        }
        for (int startRow = classAndTemplateInfo.startRowNum; startRow < lastRowNum; startRow++){
            XSSFRow row = classAndTemplateInfo.xssfSheet.getRow(startRow);
            if(row == null){
                continue;
            }
            Map<Object, Integer> compute = new HashMap<>();
            try{
                T instance = clazz.newInstance();
                for (int col = 0; col < classAndTemplateInfo.headerCells.length; col++){
                    UnitElement unitElement = classAndTemplateInfo.unitElements.get(classAndTemplateInfo.headerCells[col]);
                    if(unitElement == null){
                        continue;
                    }
                    XSSFCell cell = row.getCell(col);
                    Type type = unitElement.type;
                    if(unitElement.pulls != null){
                        type = Type.STRING;
                    }
                    Object value = CellUtil.readValue(type, cell, unitElement.field.getType());
                    setFieldValue(unitElement, value, instance, compute);
                }
                if(CollectionUtil.isNotEmpty(classAndTemplateInfo.validators)){
                    InvalidInfo invalidInfo = new InvalidInfo();
                    for (int i = 0; i < classAndTemplateInfo.validators.size(); i++){
                        ExcelDataValidator excelDataValidator = classAndTemplateInfo.validators.get(i);
                        excelDataValidator.valid(instance, invalidInfo);
                    }
                    if(invalidInfo.valid){
                        importData.addValid(instance);
                    }else {
                        importData.addInvalid(instance, invalidInfo);
                    }
                }else{
                    importData.addValid(instance);
                }
            }catch (Exception e){
                e.printStackTrace();
                throw new XlsxParseException(ErrorInfo.FIELD_VALUE_MAPPING_ERROR.getMsg() + "\n" + e.getMessage());
            }
        }
        return importData;
    }

    private static <T> void setFieldValue(UnitElement unitElement, Object value, T instance, Map<Object, Integer> compute) throws IllegalAccessException, InstantiationException, ClassNotFoundException {
        if(unitElement.parents.isEmpty() && value != null){
            unitElement.field.set(instance, value);
            return;
        }
        setMultipleFieldValue(unitElement, value, instance, compute);
    }

    private static <T> void setMultipleFieldValue(UnitElement unitElement, Object value, T instance, Map<Object, Integer> compute) throws IllegalAccessException, InstantiationException, ClassNotFoundException {
        List<ParentField> parents = unitElement.parents;
        Object lastLevel = instance;
        Object currentLevel = null;
        for (int index = 0; index < parents.size(); index++) {
            ParentField parentField = parents.get(index);
            currentLevel = getMultipleValue(unitElement.field + "" + lastLevel, lastLevel, parentField, compute);
            lastLevel = currentLevel;
        }
        if(value != null){
            setValue(lastLevel, value, unitElement.field);
        }
    }

    private static Object getMultipleValue(Object computeKey, Object parent, ParentField childField, Map<Object, Integer> compute) throws IllegalAccessException, InstantiationException, ClassNotFoundException {
        Object fieldValue = childField.field.get(parent);
        if(fieldValue == null){
            Object obj = createObj(childField, false);
            childField.field.set(parent, obj);
            fieldValue = obj;
        }
        if(fieldValue instanceof List){
            Integer computeNum = compute.get(computeKey);
            if(computeNum == null){
                computeNum = 0;
            }
            compute.put(computeKey, computeNum + 1);
            if(computeNum < ((List)fieldValue).size()){
                return ((List)fieldValue).get(computeNum);
            }else{
                Object obj = createObj(childField, true);
                ((List)fieldValue).add(obj);
                return obj;
            }
        }
        return fieldValue;
    }

    private static void setValue(Object parent, Object value, Field parentField) throws IllegalAccessException {
        if(parent instanceof List){
            ((List)parent).add(value);
        }
        parentField.set(parent, value);
    }

    private static Object createObj(ParentField field, boolean genericType) throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        if(genericType){
            String typeName = null;
            java.lang.reflect.Type type = field.field.getGenericType();
            if (type instanceof ParameterizedType) {
                java.lang.reflect.Type[] actualTypeArguments = ((ParameterizedType) type).getActualTypeArguments();
                if(!ArrayUtil.isNotEmpty(actualTypeArguments)){
                    throw new RuntimeException(ErrorInfo.FIELD_GENERIC_TYPE_MISSING.getMsg());
                }
                typeName = ((ParameterizedType)type).getActualTypeArguments()[0].getTypeName();
            }
            Class classForName = CLASS_FOR_NAME.get(typeName);
            if(classForName == null){
                classForName = Class.forName(typeName);
                CLASS_FOR_NAME.put(typeName, classForName);
            }
            return classForName.newInstance();
        }
        Class subclass = field.subclass;
        if(!Object.class.equals(subclass)){
            return subclass.newInstance();
        }
        return field.field.getType().newInstance();
    }
}
