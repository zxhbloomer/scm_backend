package com.xinyirun.scm.excel.upload;

import com.xinyirun.scm.excel.bean.importconfig.template.ExcelTemplate;
import com.xinyirun.scm.excel.bean.importconfig.template.title.TitleRow;

import java.util.List;

/**
 *  excel
 * @author zxh
 */
public class SystemExcelBase {

    public static final int MAX_ROW_PER_SHEET = 60000;
    protected ExcelTemplate excelTemplate;

    /**
     * 设置excel导入模板
     * @param excelTemplate
     */
    public void setExcelTemplate(ExcelTemplate excelTemplate) {
        this.excelTemplate = excelTemplate;
    }

    /**
     * check模板是否有问题
     */
    protected void checkTemplate() {
        checkTemplateNotNull();
        checkTemplateColSize();
    }

    /**
     * 判断模板是否为空
     */
    private void checkTemplateNotNull() {
        if (excelTemplate == null) {
            throw new SystemExcelException("excel模板配置未初始化");
        }
    }

    /**
     * 判断模板中是否包含标题行
     */
    private void checkTemplateColSize() {
        List<TitleRow> titleRows = excelTemplate.getTitleRows();
        if (titleRows.size() < 1) {
            throw new SystemExcelException("必须至少定义一个标题行");
        }
        if (titleRows.size() == 1) {
            return;
        }
        int colSize = titleRows.get(0).colSize();
        for (int i = 1; i < titleRows.size(); i++) {
            TitleRow titleRow = titleRows.get(i);
            if (colSize != titleRow.colSize()) {
                throw new SystemExcelException("标题行列数必须一致");
            }
        }
        if (excelTemplate.getDataRows() != null) {
            if (colSize != excelTemplate.getDataRows().colSize()) {
                throw new SystemExcelException("标题行列数必须与数据行列数一致");
            }
        }
    }
}
