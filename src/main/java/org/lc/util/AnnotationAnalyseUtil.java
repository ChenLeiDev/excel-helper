package org.lc.util;

import org.lc.ExcelDataValidator;
import org.lc.ExcelPullTransfer;
import org.lc.annotation.ExcelColumn;
import org.lc.annotation.ExcelDynamicModel;
import org.lc.annotation.ExcelFunction;
import org.lc.config.ExcelHelperAutoConfiguration;
import org.lc.constant.AnnotationConstants;
import org.lc.constant.Function;
import org.lc.exception.ErrorInfo;
import org.lc.exception.XlsxParseException;
import org.lc.model.*;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

@Component
public class AnnotationAnalyseUtil{

    public static <T> void analyseAnnotation(Class<T> clazz, ClassAndTemplateInfo classAndTemplateInfo, DynamicColumn dynamicColumn, Class<? extends ExcelPullTransfer> excelPullTransferClass, Class<? extends ExcelDataValidator>... excelDataValidatorClass) throws XlsxParseException {
        recursionAnalyseClass(clazz, classAndTemplateInfo, null, 0, dynamicColumn, excelPullTransferClass);
        if(ArrayUtil.isNotEmpty(excelDataValidatorClass)){
            for (int i = 0; i < excelDataValidatorClass.length; i++){
                if(excelDataValidatorClass[i] != null){
                    if(classAndTemplateInfo.validators == null){
                        classAndTemplateInfo.validators = new ArrayList<>();
                    }
                    ExcelDataValidator bean = ExcelHelperAutoConfiguration.getApplicationContext().getBean(excelDataValidatorClass[i]);
                    classAndTemplateInfo.validators.add(bean);
                }
            }
        }
    }

    private static int recursionAnalyseClass(Class clazz, ClassAndTemplateInfo classAndTemplateInfo, UnitElement unitElement, int level, DynamicColumn dynamicColumn, Class<? extends ExcelPullTransfer> excelPullTransferClass) throws XlsxParseException {
        List<Field> fields = getOwnExcelColumnFields(null, clazz);
        for (Field field: fields) {
            if(0 == level || unitElement == null){
                unitElement = new UnitElement();//设置对应列所需要的信息
            }
            ExcelDynamicModel dynamicModel = field.getDeclaredAnnotation(ExcelDynamicModel.class);
            if(dynamicModel != null){
                Class subclass = dynamicModel.subclass();
                Type fieldType = field.getGenericType();
                if(fieldType instanceof ParameterizedType){
                    ParameterizedType parameterizedType = (ParameterizedType)fieldType;
                    Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
                    if(ArrayUtil.isNotEmpty(actualTypeArguments)){
                        fieldType = actualTypeArguments[0];
                    }
                }
                String typeName = fieldType.getTypeName();
                Class<?> subSetClass = null;
                try{
                    subSetClass = Class.forName(typeName);//此处不会报错
                }catch (ClassNotFoundException e){
                    e.printStackTrace();
                }
                unitElement.parents.add(new ParentField(subclass, field));
                int dealLevel = recursionAnalyseClass(subSetClass, classAndTemplateInfo, unitElement, level + 1, dynamicColumn, excelPullTransferClass);
                int levelReduce = dealLevel - level;
                if (levelReduce > 1){
                    unitElement.parents = unitElement.parents.subList(0, unitElement.parents.size() - levelReduce);
                }
                continue;
            }
            ExcelColumn excelColumn = field.getDeclaredAnnotation(ExcelColumn.class);
            ExcelFunction excelFunction = field.getDeclaredAnnotation(ExcelFunction.class);
            if(excelColumn != null){
                unitElement.field = field;
                String column = excelColumn.column();
                unitElement.column = column;
                unitElement.type = excelColumn.type();
                if(!AnnotationConstants.DEFAULT_DATA_FORMAT_PATTERN.equals(unitElement.format)){
                    unitElement.format = excelColumn.format();
                }
                String pullsFlag = excelColumn.pulls();
                if(excelPullTransferClass != null){
                    if(!AnnotationConstants.DEFAULT_PULLS_FLAG.equals(pullsFlag)){//获取下拉框
                        ExcelPullTransfer excelPullTransfer = ExcelHelperAutoConfiguration.getApplicationContext().getBean(excelPullTransferClass);
                        if(excelPullTransfer != null){
                            if(classAndTemplateInfo.pullNodes == null){
                                classAndTemplateInfo.pullNodes = new PullNodes();
                            }
                            classAndTemplateInfo.pullNodes.pullsFlag = pullsFlag;
                            excelPullTransfer.pulls(pullsFlag, classAndTemplateInfo.pullNodes);
                            unitElement.pullsFlag = pullsFlag;
                            unitElement.pulls = classAndTemplateInfo.pullNodes.pulls();
                        }
                    }
                }
                //处理函数注解
                if(excelFunction != null){
                    Function function = excelFunction.function();
                    String condition = excelFunction.condition();
                    unitElement.function = function;
                    if(function.getHasIf()){
                        unitElement.condition = condition;
                    }
                    if(classAndTemplateInfo.unitElements.get(UnitElement.HAS_FUNCTION) == null){
                        classAndTemplateInfo.unitElements.put(UnitElement.HAS_FUNCTION, new UnitElement());
                    }
                }
                if(dynamicColumn != null){
                    List<String> headersByDynamicColumn = dynamicColumn.getHeadersByDynamicColumn(column);
                    if(!headersByDynamicColumn.isEmpty()){
                        for (String header: headersByDynamicColumn) {
                            putUnitElement(classAndTemplateInfo, header, unitElement);
                        }
                    }else{
                        putUnitElement(classAndTemplateInfo, column, unitElement);
                    }
                }else{
                    putUnitElement(classAndTemplateInfo, column, unitElement);
                }
                List<ParentField> parent = unitElement.parents;
                unitElement = new UnitElement();
                unitElement.parents.addAll(parent);
            }
        }
        return level;
    }

    private static void putUnitElement(ClassAndTemplateInfo classAndTemplateInfo, String column, UnitElement unitElement) throws XlsxParseException {
        if(classAndTemplateInfo.unitElements.get(column) != null){
            throw new XlsxParseException(ErrorInfo.COLUMN_DUPLICATE.getMsg());
        }
        classAndTemplateInfo.unitElements.put(column, unitElement);
    }

    private static <T> List<Field> getOwnExcelColumnFields(List<Field> fields, Class<T> clazz){
        if(fields == null){
            fields = new ArrayList<>();
        }
        Field[] declaredFields = clazz.getDeclaredFields();
        for (Field field: declaredFields) {
            field.setAccessible(true);
            fields.add(field);
        }
        Class<? super T> superclass = clazz.getSuperclass();
        if(superclass != null){
            return getOwnExcelColumnFields(fields, superclass);
        }
        return fields;
    }
}
