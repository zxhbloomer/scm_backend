package com.xinyirun.scm.excel.conf;

import com.xinyirun.scm.common.utils.string.StringUtils;
import com.xinyirun.scm.excel.bean.importconfig.template.data.DataCol;
import com.xinyirun.scm.excel.bean.importconfig.template.data.DataRow;
import com.xinyirun.scm.excel.bean.importconfig.template.ExcelTemplate;
import com.xinyirun.scm.excel.bean.importconfig.template.title.TitleRow;
import com.xinyirun.scm.excel.conf.validator.Validator;
import com.xinyirun.scm.excel.conf.validator.ValidatorUtil;
import com.xinyirun.scm.excel.upload.SystemExcelException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.BeanUtilsBean;
import org.apache.commons.beanutils.BeanUtilsBean2;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.HierarchicalConfiguration;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.commons.io.filefilter.NameFileFilter;
import org.apache.commons.io.monitor.FileAlterationListenerAdaptor;
import org.apache.commons.io.monitor.FileAlterationMonitor;
import org.apache.commons.io.monitor.FileAlterationObserver;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * excel模板工厂类
 * @author zhangxh
 */
@Deprecated
@Slf4j
public class ExcelTemplateFactory {

    private static ExcelTemplateFactory instance = null;
    private static Log logger = LogFactory.getLog(ExcelTemplateFactory.class);
    private final long refreshDelay = 30000;
    private Map<String, ExcelTemplate> templateMap = new HashMap<String, ExcelTemplate>();

    private ExcelTemplateFactory() {
        BeanUtilsBean.setInstance(new BeanUtilsBean2());
        loadTemplates();
        monitorConfigFile();
    }

    public static ExcelTemplateFactory getInstance() {
        if (instance == null) {
            instance = new ExcelTemplateFactory();
        }
        return instance;
    }

    private void monitorConfigFile() {
        FileAlterationObserver observer = new FileAlterationObserver(getClass().getResource("/").getFile(), new NameFileFilter("jxl-excel.xml"));
        FileAlterationListenerAdaptor listener = new FileAlterationListenerAdaptor() {
            @Override
            public void onFileChange(File file) {
                try {
                    loadTemplates();
                } catch (SystemExcelException e) {
                    throw new RuntimeException(e);
                }
            }
        };
        observer.addListener(listener);
        final FileAlterationMonitor monitor = new FileAlterationMonitor(refreshDelay, observer);
        try {
            monitor.start();
            Runtime.getRuntime().addShutdownHook(new Thread() {
                @Override
                public void run() {
                    try {
                        monitor.stop();
                    } catch (Exception e) {
                        log.error("monitorConfigFile error", e);
                    }
                }
            });
        } catch (Exception e) {
            throw new SystemExcelException(e);
        }
    }

    private void loadTemplates() {
        try {
            logger.debug("loadTemplates");
            templateMap.clear();
            XMLConfiguration xmlConfig = new XMLConfiguration(getClass().getResource("/jxl-excel.xml"));
            List<HierarchicalConfiguration> templates = xmlConfig.configurationsAt("template");
            for (HierarchicalConfiguration templateConf : templates) {
                String templateName = templateConf.getString("[@name]");
                if (StringUtils.isEmpty(templateName)) {
                    throw new SystemExcelException("模板的名称属性name不能为空");
                }
                ExcelTemplate excelTemplate = new ExcelTemplate();
                excelTemplate.setName(templateName);
                List<HierarchicalConfiguration> titleRows = templateConf.configurationsAt("titleRow");
                for (HierarchicalConfiguration titleRowConf : titleRows) {
                    TitleRow titleRow = new TitleRow();
                    List<HierarchicalConfiguration> titleCols = titleRowConf.configurationsAt("titleCol");
                    for (HierarchicalConfiguration titleColConf : titleCols) {
                        Integer span = titleColConf.getInteger("[@span]", 1);
                        String title = titleColConf.getString("");
                        titleRow.addCol(title, span);
                    }
                    excelTemplate.addTitleRow(titleRow);
                }
                HierarchicalConfiguration dataRowConfig = templateConf.configurationsAt("dataRow").get(0);
                List<HierarchicalConfiguration> dataCols = dataRowConfig.configurationsAt("dataCol");
                DataRow dataRow = new DataRow();
                for (HierarchicalConfiguration dataColConf : dataCols) {
                    DataCol dataCol = new DataCol(dataColConf.getString("[@name]"));
                    String convertor = dataColConf.getString("[@convertor]");
                    if (StringUtils.isNotEmpty(convertor)) {
                        dataCol.setConvertor(convertor);
                    }
                    List<HierarchicalConfiguration> validatorConfs = dataColConf.configurationsAt("validator");
                    for (HierarchicalConfiguration validatorConf : validatorConfs) {
                        Validator validator = ValidatorUtil.getValidator(validatorConf.getString("[@name]"));
                        List<HierarchicalConfiguration> propertyConfs = validatorConf.configurationsAt("property");
                        dataCol.addValidator(validator);
                        for (HierarchicalConfiguration propertyConf : propertyConfs) {
                            try {
                                BeanUtils.setProperty(validator, propertyConf.getString("[@name]"), propertyConf.getString(""));
                            } catch (IllegalAccessException e) {
                                throw new SystemExcelException(e);
                            } catch (InvocationTargetException e) {
                                throw new SystemExcelException(e);
                            }
                        }
                    }
                    dataRow.addDataCol(dataCol);
                }
                excelTemplate.setDataRows(dataRow);
                templateMap.put(templateName, excelTemplate);
                logger.debug(String.format("加载了excel模板：%s", templateName));
            }
        } catch (ConfigurationException e) {
            logger.warn("未在classpath路径下找到jxl-excel.xml文件，将不能使用配置模式配置excel模板");
        }
    }

    public ExcelTemplate getTemplate(String templateName) {
        if (!templateMap.containsKey(templateName)) {
            throw new SystemExcelException(String.format("名称为%s的模板不存在", templateName));
        }
        return templateMap.get(templateName);
    }

}
