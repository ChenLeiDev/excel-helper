package org.lc;

import org.springframework.beans.BeansException;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Import;

@Import({ExcelHelper.class})
@EnableConfigurationProperties({ApplicationExcelHelperAutoConfig.class})
public class ApplicationExcelHelperAutoConfig implements ApplicationContextAware {

    private static ApplicationContext applicationContext = null;

    public static ApplicationContext getApplicationContext(){
        return applicationContext;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        ApplicationExcelHelperAutoConfig.applicationContext = applicationContext;
    }
}
