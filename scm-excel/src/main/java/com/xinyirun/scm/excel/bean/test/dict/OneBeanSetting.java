package com.xinyirun.scm.excel.bean.test.dict;

import com.xinyirun.scm.excel.bean.importconfig.template.data.DataCol;
import com.xinyirun.scm.excel.bean.importconfig.template.data.DataRow;
import com.xinyirun.scm.excel.bean.importconfig.template.title.TitleCol;
import com.xinyirun.scm.excel.bean.importconfig.template.title.TitleRow;
import com.xinyirun.scm.excel.bean.importconfig.template.validator.NameAndValue;
import com.xinyirun.scm.excel.bean.importconfig.template.validator.ValidatorBean;
import com.xinyirun.scm.excel.conf.constant.ExcelImportConvertorsConstants;
import com.xinyirun.scm.excel.conf.constant.ExcelImportValidatorConstants;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zxh
 * @date 2019年 08月06日 21:38:13
 */
public class OneBeanSetting {

    public List<TitleRow> getTitleRows(){
        List<TitleRow> lst = new ArrayList<>();
        lst.add(getTitleRow());
        return lst;
    }

    public TitleRow getTitleRow(){
        TitleRow titleRow = new TitleRow();
        List<TitleCol> cols = new ArrayList<>();

        TitleCol titleCol1 = new TitleCol("角色类型");
        TitleCol titleCol2 = new TitleCol("角色编码");
        TitleCol titleCol3 = new TitleCol("角色名称");
        TitleCol titleCol4 = new TitleCol("说明");
        TitleCol titleCol5 = new TitleCol("简称");

        cols.add(titleCol1);
        cols.add(titleCol2);
        cols.add(titleCol3);
        cols.add(titleCol4);
        cols.add(titleCol5);

        titleRow.setCols(cols);
        return titleRow;
    }

    public DataRow getDataRow(){
        DataRow row = new DataRow();

        DataCol dataCol1 = new DataCol("type");
        DataCol dataCol2 = new DataCol("code");
        dataCol2.setConvertor(ExcelImportConvertorsConstants.CONVERTOR_DATE);
        // 添加类
        List<ValidatorBean> listValiDatorBean1 = new ArrayList<>();
        listValiDatorBean1.add(getValidatorRequired());
        listValiDatorBean1.add(getValidatorDateTime());
        // 添加验证
//        dataCol2.setListValidators(listValiDatorBean1);

        DataCol dataCol3 = new DataCol("name");
        List<ValidatorBean> listValiDatorBean2 = new ArrayList<>();
        listValiDatorBean2.add(getValidatorReflection());
        dataCol3.setListValidators(listValiDatorBean2);

        DataCol dataCol4 = new DataCol("descr");
        DataCol dataCol5 = new DataCol("simpleName");

        row.addDataCol(dataCol1);
        row.addDataCol(dataCol2);
        row.addDataCol(dataCol3);
        row.addDataCol(dataCol4);
        row.addDataCol(dataCol5);

        return row;
    }

    /**
     * 必须输入的校验
     * @return
     */
    private ValidatorBean getValidatorRequired(){
        ValidatorBean bean = new ValidatorBean();
        bean.setValidtorName(ExcelImportValidatorConstants.VALIDATOR_REQUIRED);
        return bean;
    }

    /**
     * 反射check
     * @return
     */
    private ValidatorBean getValidatorReflection(){
        ValidatorBean bean = new ValidatorBean();
        bean.setValidtorName(ExcelImportValidatorConstants.VALIDATOR_REFLECTION);
        List<NameAndValue> param = new ArrayList<>();

        NameAndValue className = new NameAndValue();
        // 设置参数中的field名称
        className.setName("className");
        // 设置参数中的field的值
        className.setValue("com.xinyirun.scm.core.logic.system.v1.serviceimpl.sys.config.dict.SDictTypeServiceImpl");
        param.add(className);

        NameAndValue functionName = new NameAndValue();
        // 设置参数中的field名称
        functionName.setName("functionName");
        // 设置参数中的field的值
        functionName.setValue("testCheck");
        param.add(functionName);

        bean.setParam(param);

        return bean;
    }

    /**
     * 必须输入的校验
     * @return
     */
    private ValidatorBean getValidatorDateTime(){
        ValidatorBean bean = new ValidatorBean();
        bean.setValidtorName(ExcelImportValidatorConstants.VALIDATOR_DATETIME);

        List<NameAndValue> param = new ArrayList<>();
        NameAndValue nv = new NameAndValue();
        // 设置参数中的field名称
        nv.setName("dateFormat");
        // 设置参数中的field的值
        nv.setValue("yyyy-MM-dd HH:mm:ss");
        param.add(nv);

        bean.setParam(param);
        return bean;
    }
}
