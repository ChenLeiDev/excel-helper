package org.lc;

import org.apache.tomcat.util.http.fileupload.FileItem;
import org.lc.exception.XlsxParseException;
import org.lc.model.ClassAndTemplateInfo;
import org.lc.model.DynamicColumn;
import org.lc.model.ImportData;
import org.lc.model.XlsxFile;
import org.lc.util.AnnotationAnalyseUtil;
import org.lc.util.TemplateAnalyseUtil;
import org.lc.util.excel.ExcelData;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

public class ExcelHelper {

    public static final String XLSX_FILE_SUFFIX = ".xlsx";
    private static final String CONTENT_TYPE = "application/vnd.ms-excel;charset=utf-8";
    private static final String CHARSET = "UTF-8";
    private static final String HEADER_KEY = "Content-Disposition";
    private static final String HEADER_VALUE = "attachment;filename=";
    public static final String HIDDEN_PULLS_SHEET_PREFIX = "HIDDEN_PULLS_SHEET_";


    /**
     * 导出
     * @param data 数据列表
     * @param type 注解类类型
     * @param <T>
     */
    public static <T> XlsxFile exportXlsx(List data, Class<T> type) throws XlsxParseException {
        return exportXlsx(data, type, null, null);
    }

    /**
     * 导出 +下拉转换
     * @param data 数据列表
     * @param type 注解类类型
     * @param excelPullTransferClass 下拉转换实现类
     * @param <T>
     */
    public static <T> XlsxFile exportXlsx(List data, Class<T> type, Class<? extends ExcelPullTransfer> excelPullTransferClass) throws XlsxParseException {
        return exportXlsx(data, type, excelPullTransferClass, null);
    }

    /**
     * 导出 + 动态列
     * @param data 数据列表
     * @param type 注解类类型
     * @param dynamicColumn 动态列对象
     * @param <T>
     */
    public static <T> XlsxFile exportXlsx(List data, Class<T> type, DynamicColumn dynamicColumn) throws XlsxParseException {
        return exportXlsx(data, type, null, dynamicColumn);
    }

    /**
     * 导出 + 下拉转换 + 动态列
     * @param data 数据列表
     * @param type 注解类类型
     * @param excelPullTransferClass 下拉转换实现类
     * @param dynamicColumn 动态列对象
     * @param <T>
     */
    public static <T> XlsxFile exportXlsx(List data, Class<T> type, Class<? extends ExcelPullTransfer> excelPullTransferClass, DynamicColumn dynamicColumn) throws XlsxParseException {
        ClassAndTemplateInfo classAndTemplateInfo = new ClassAndTemplateInfo();
        AnnotationAnalyseUtil.analyseAnnotation(type, classAndTemplateInfo, dynamicColumn, excelPullTransferClass);
        TemplateAnalyseUtil.analyseTemplate(type, classAndTemplateInfo, dynamicColumn, null);
        if(classAndTemplateInfo.pullNodes != null){
            ExcelData.setPulls(data == null ? 0 : data.size(), classAndTemplateInfo);
        }
        ExcelData.setRowStyle(data == null ? 0 : data.size(), classAndTemplateInfo);
        try{
            ExcelData.writeDataTask(data, classAndTemplateInfo);
        }catch (XlsxParseException e) {
            try{
                classAndTemplateInfo.xssfWorkbook.close();
            }catch (IOException ioe){
                ioe.printStackTrace();
            }
            throw e;
        }
        classAndTemplateInfo.xssfWorkbook.setForceFormulaRecalculation(true);// 执行公式
        FileItem fileItem = TemplateAnalyseUtil.createFileItem(classAndTemplateInfo.templateName);
        OutputStream outputStream = null;
        boolean exc = false;
        try{
            outputStream = fileItem.getOutputStream();
            classAndTemplateInfo.xssfWorkbook.write(outputStream);
            outputStream.flush();
            return new XlsxFile(fileItem);
        } catch (IOException e) {
            exc = true;
            e.printStackTrace();
            return null;
        }finally {
            if(outputStream != null){
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            try {
                classAndTemplateInfo.xssfWorkbook.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if(exc){
                fileItem.delete();
            }
        }
    }

    /**
     * 导入
     * @param type 注解类类型
     * @param <T>
     * @return
     */
    public static <T> ImportData<T> importXlsx(Class<T> type, InputStream inputStream, Class<? extends ExcelDataValidator>... excelDataValidatorClass) throws XlsxParseException {
        return importXlsx(type, inputStream, null, null, excelDataValidatorClass);
    }

    /**
     * 导入 + 下拉转换
     * @param type 注解类类型
     * @param excelPullTransferClass 下拉转换实现类
     * @param <T>
     * @return
     */
    public static <T> ImportData<T> importXlsx(Class<T> type, InputStream inputStream, Class<? extends ExcelPullTransfer> excelPullTransferClass, Class<? extends ExcelDataValidator>... excelDataValidatorClass) throws XlsxParseException {
        return importXlsx(type, inputStream, excelPullTransferClass, null, excelDataValidatorClass);
    }

    /**
     * 导入 + 动态导入
     * @param type 注解类类型
     * @param dynamicColumn 动态列对象
     * @param <T>
     * @return
     */
    public static <T> ImportData<T> importXlsx(Class<T> type, InputStream inputStream, DynamicColumn dynamicColumn, Class<? extends ExcelDataValidator>... excelDataValidatorClass) throws XlsxParseException {
        return importXlsx(type, inputStream, null, dynamicColumn, excelDataValidatorClass);
    }

    /**
     * 导入 + 下拉转换 + 动态导入
     * @param type 注解类类型
     * @param excelPullTransferClass 下拉转换实现类
     * @param dynamicColumn 动态列对象
     * @param <T>
     * @return
     */
    public static <T> ImportData<T> importXlsx(Class<T> type, InputStream inputStream, Class<? extends ExcelPullTransfer> excelPullTransferClass, DynamicColumn dynamicColumn, Class<? extends ExcelDataValidator>... excelDataValidatorClass) throws XlsxParseException {
        ClassAndTemplateInfo classAndTemplateInfo = new ClassAndTemplateInfo();
        AnnotationAnalyseUtil.analyseAnnotation(type, classAndTemplateInfo, dynamicColumn, excelPullTransferClass, excelDataValidatorClass);
        TemplateAnalyseUtil.analyseTemplate(type, classAndTemplateInfo, dynamicColumn, inputStream);
        return ExcelData.readData(type, classAndTemplateInfo);
    }

    /**
     * 给response对象设置文件信息
     * @param response
     * @param name 导出文件名，不需要带后缀
     */
    static void setExportName(HttpServletResponse response, String name){
        name = TemplateAnalyseUtil.buildName(name);
        response.setContentType(CONTENT_TYPE);
        try{
            name = URLEncoder.encode(name, CHARSET);
        }catch (UnsupportedEncodingException e){}
        response.setHeader(HEADER_KEY, HEADER_VALUE.concat(name));
    }

}
