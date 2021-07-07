package org.lc.config;

import org.lc.util.StringUtil;
import org.springframework.beans.BeansException;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Configuration;


@Configuration
@EnableConfigurationProperties({ExcelHelperConfigProperties.class})
public class ExcelHelperAutoConfiguration implements ApplicationContextAware {

    private static ApplicationContext applicationContext = null;
    private static Boolean multiThread = false;
    private static Integer multiThreadNum = 16;
    private static Integer pageLimit = 10000;
    private static String templatePath = "static/template/";
    private static String formFieldName = "file";

    public static ApplicationContext getApplicationContext(){
        return applicationContext;
    }

    public static Boolean getMultiThread() {
        return multiThread;
    }

    public static Integer getPageLimit() {
        return pageLimit;
    }

    public static Integer getMultiThreadNum() {
        return multiThreadNum;
    }

    public static String getTemplatePath() {
        return templatePath;
    }

    public static String getFormFieldName() {
        return formFieldName;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        ExcelHelperAutoConfiguration.applicationContext = applicationContext;
        if(applicationContext != null){
            ExcelHelperConfigProperties excelHelperConfigProperties = applicationContext.getBean(ExcelHelperConfigProperties.class);
            String formFieldName = excelHelperConfigProperties.getFormFieldName();
            if(StringUtil.isNotBlank(formFieldName)){
                ExcelHelperAutoConfiguration.formFieldName = formFieldName;
            }
            Boolean multiThread = excelHelperConfigProperties.getMultiThread();
            if(multiThread != null){
                ExcelHelperAutoConfiguration.multiThread = multiThread;
            }
            Integer multiThreadNum = excelHelperConfigProperties.getMultiThreadNum();
            if(multiThreadNum != null && multiThreadNum > 0){
                ExcelHelperAutoConfiguration.multiThreadNum = multiThreadNum;
            }
            Integer pageLimit = excelHelperConfigProperties.getPageLimit();
            if(pageLimit != null && pageLimit > 1){
                ExcelHelperAutoConfiguration.pageLimit = pageLimit;
            }
            String templatePath = excelHelperConfigProperties.getTemplatePath();
            if(StringUtil.isNotBlank(templatePath)){
                ExcelHelperAutoConfiguration.templatePath = templatePath;
            }
        }

    }
}
