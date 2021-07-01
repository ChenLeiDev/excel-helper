package org.lc;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.concurrent.ThreadPoolExecutor;

public abstract class ExcelHelperConfiguration implements ApplicationContextAware {


    private static ThreadPoolExecutor ASYNC_TASK_POOL = null;
    private static int DEFAULT_PAGE_LIMIT = 10000;
    private static String LOCAL_TEMPLATE_FILE_PATH = "C:\\Users\\ChenLei\\Desktop\\";
    private static String TEMPLATE_FILE_PATH = "static/template/";


    private final void initEnv(){
        ASYNC_TASK_POOL = initAsyncPool();
        int pageLimit = setPageLimit();
        if(pageLimit > 0){
            DEFAULT_PAGE_LIMIT = pageLimit;
        }
        setTemplateFilePath(TEMPLATE_FILE_PATH);
        setLocalTemplateFilePath(LOCAL_TEMPLATE_FILE_PATH);
    }

    protected abstract int setPageLimit();

    protected abstract ThreadPoolExecutor initAsyncPool();

    public static final boolean getRunAsync(){
        return ASYNC_TASK_POOL == null ? false : true;
    }

    public static final ThreadPoolExecutor getAsyncTaskPool(){
        return ASYNC_TASK_POOL;
    }

    public static final int getPageLimit(){
        return DEFAULT_PAGE_LIMIT;
    }

    public static String getLocalTemplateFilePath() {
        return LOCAL_TEMPLATE_FILE_PATH;
    }

    protected void setLocalTemplateFilePath(String localTemplateFilePath){
        LOCAL_TEMPLATE_FILE_PATH = localTemplateFilePath;
    }

    public static String getTemplateFilePath() {
        return TEMPLATE_FILE_PATH;
    }

    protected void setTemplateFilePath(String templateFilePath) {
        TEMPLATE_FILE_PATH = templateFilePath;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        ExcelHelperConfiguration bean = applicationContext.getBean(ExcelHelperConfiguration.class);
        if(bean != null){
            bean.initEnv();
        }
    }
}
