package org.lc.util;

import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.tomcat.util.http.fileupload.FileItem;
import org.apache.tomcat.util.http.fileupload.disk.DiskFileItemFactory;
import org.lc.ExcelHelper;
import org.lc.ExcelHelperConfiguration;
import org.lc.annotation.ExcelModel;
import org.lc.constant.AnnotationConstants;
import org.lc.model.ClassAndTemplateInfo;
import org.lc.model.DynamicColumn;
import org.lc.util.excel.RowUtil;
import org.springframework.core.io.ClassPathResource;

import java.io.*;
import java.util.List;
import java.util.UUID;

public class TemplateAnalyseUtil {

    private static final DiskFileItemFactory DISK_FILE_ITEM_FACTORY = new DiskFileItemFactory(4, null);

    private TemplateAnalyseUtil(){}

    public static <T> void analyseTemplate(Class<T> clazz, ClassAndTemplateInfo classAndTemplateInfo, DynamicColumn dynamicColumn, InputStream inputStream){
        ExcelModel excelModel = clazz.getDeclaredAnnotation(ExcelModel.class);
        String templateFileName = excelModel.template();
        templateFileName = TemplateAnalyseUtil.buildName(templateFileName);
        classAndTemplateInfo.templateName = templateFileName;
        FileItem templateFile = TemplateAnalyseUtil.getTemplateFile(templateFileName, inputStream);
        int headerRowNum = excelModel.header();
        classAndTemplateInfo.headerRowNum = headerRowNum;
        int startRowNum = excelModel.start();
        if(AnnotationConstants.DEFAULT_DATA_START_ROW_NUM == startRowNum){
            startRowNum = headerRowNum + 1;
        }
        classAndTemplateInfo.startRowNum = startRowNum;
        String sheetName = excelModel.sheet();
        InputStream templateInput = null;
        XSSFWorkbook xssfWorkbook = null;
        try{
            templateInput = templateFile.getInputStream();
            xssfWorkbook = new XSSFWorkbook(templateInput);
        }catch (IOException ioe){
            if(xssfWorkbook != null){
                try {
                    xssfWorkbook.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }finally {
            if(templateInput != null){
                try {
                    templateInput.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
            templateFile.delete();
        }
        classAndTemplateInfo.xssfWorkbook = xssfWorkbook;
        XSSFSheet sheet = xssfWorkbook.getSheet(sheetName);
        if(sheet == null){
            sheet = xssfWorkbook.createSheet(sheetName);
            XSSFRow headerRow = sheet.createRow(headerRowNum);
            RowUtil.writeHeaders(headerRow, classAndTemplateInfo.unitElements.keySet(), 0);
        }
        classAndTemplateInfo.xssfSheet = sheet;
        classAndTemplateInfo.drawingPatriarch = classAndTemplateInfo.xssfSheet.createDrawingPatriarch();
        XSSFRow headerRow = sheet.getRow(headerRowNum);
        if(dynamicColumn != null){
            List<String> dynamicColumnHeaders = dynamicColumn.getHeaders();
            RowUtil.writeHeaders(headerRow, dynamicColumnHeaders, headerRow.getLastCellNum());
        }
        short lastCellNum = headerRow.getLastCellNum();
        classAndTemplateInfo.headerCells = new String[headerRow.getLastCellNum()];
        for (int cellNum = 0; cellNum < lastCellNum; cellNum++){
            XSSFCell headerRowCell = headerRow.getCell(cellNum);
            if(headerRowCell != null){
                String headerValue = headerRowCell.getStringCellValue();
                if(StringUtil.isNotBlank(headerValue)){
                    classAndTemplateInfo.headerCells[cellNum] = headerValue;
                }
            }
        }
    }

    public static FileItem getTemplateFile(String name, InputStream inputStream) {
        if(inputStream != null){
            return getTemplateFileFromStream(name, inputStream);
        }
        boolean startupFromJar = EnvironmentUtil.isStartupFromJar(TemplateAnalyseUtil.class);
        if (startupFromJar) {
            return getTemplateFielFromJar(name);
        }
        boolean startupFromFile = EnvironmentUtil.isStartupFromFile(TemplateAnalyseUtil.class);
        if (startupFromFile) {
            return getTemplateFielFromFile(name);
        }
        return createFileItem(name);
    }

    public static FileItem getTemplateFileFromStream(String name, InputStream templateInput){
        FileItem temp = null;
        OutputStream tempOutput = null;
        try{
            temp = createFileItem(name);
            tempOutput = temp.getOutputStream();
            byte[] buffer = new byte[1024];
            while (templateInput.read(buffer) > -1) {
                tempOutput.write(buffer);
                tempOutput.flush();
            }
            return temp;
        }catch (IOException ioe){
            ioe.printStackTrace();
            return null;
        }finally {
            if(tempOutput != null){
                try {
                    tempOutput.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(templateInput != null){
                try {
                    templateInput.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static FileItem getTemplateFielFromFile(String name){
        File template = new File(ExcelHelperConfiguration.getLocalTemplateFilePath().concat(name));
        if(!template.exists()){
            return null;
        }
        FileItem temp = createFileItem(name);
        InputStream templateInput = null;
        OutputStream tempOutput = null;
        try{
            templateInput = new FileInputStream(template);
            tempOutput = temp.getOutputStream();
            byte[] buffer = new byte[1024];
            while (templateInput.read(buffer) > -1) {
                tempOutput.write(buffer);
                tempOutput.flush();
            }
            return temp;
        }catch (IOException ioe){
            ioe.printStackTrace();
            return null;
        }finally {
            if(tempOutput != null){
                try {
                    tempOutput.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(templateInput != null){
                try {
                    templateInput.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static FileItem getTemplateFielFromJar(String name){
        ClassPathResource classPathResource = new ClassPathResource(ExcelHelperConfiguration.getTemplateFilePath().concat(name));
        FileItem temp = null;
        InputStream templateInput = null;
        OutputStream tempOutput = null;
        try{
            templateInput = classPathResource.getInputStream();
            temp = createFileItem(name);
            tempOutput = temp.getOutputStream();
            byte[] buffer = new byte[1024];
            while (templateInput.read(buffer) > -1) {
                tempOutput.write(buffer);
                tempOutput.flush();
            }
            return temp;
        }catch (IOException ioe){
            ioe.printStackTrace();
            return null;
        }finally {
            if(tempOutput != null){
                try {
                    tempOutput.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(templateInput != null){
                try {
                    templateInput.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static FileItem createFileItem(String name) {
        return DISK_FILE_ITEM_FACTORY.createItem("file", "application/msword", true, name);
    }

    public static String buildName(String name){
        if(name.contains(".")){
            name = name.substring(0, name.indexOf(".", 0));
        }
        if(!StringUtil.isNotBlank(name)){
            name = UUID.randomUUID().toString();
        }
        return name.concat(ExcelHelper.XLSX_FILE_SUFFIX);
    }
}
