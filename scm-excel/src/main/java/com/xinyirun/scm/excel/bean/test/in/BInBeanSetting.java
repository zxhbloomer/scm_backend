package com.xinyirun.scm.excel.bean.test.in;

import com.xinyirun.scm.excel.bean.importconfig.template.data.DataCol;
import com.xinyirun.scm.excel.bean.importconfig.template.data.DataRow;
import com.xinyirun.scm.excel.bean.importconfig.template.data.row.DataRowValidators;
import com.xinyirun.scm.excel.bean.importconfig.template.title.TitleCol;
import com.xinyirun.scm.excel.bean.importconfig.template.title.TitleRow;
import com.xinyirun.scm.excel.bean.importconfig.template.validator.NameAndValue;
import com.xinyirun.scm.excel.bean.importconfig.template.validator.ValidatorBean;
import com.xinyirun.scm.excel.conf.constant.ExcelImportConvertorsConstants;
import com.xinyirun.scm.excel.conf.constant.ExcelImportValidatorConstants;

import java.util.ArrayList;
import java.util.List;

/**
 */
public class BInBeanSetting {

    public List<TitleRow> getTitleRows(){
        List<TitleRow> lst = new ArrayList<>();
        lst.add(getTitleRow());
        return lst;
    }

    public TitleRow getTitleRow(){
        TitleRow titleRow = new TitleRow();
        List<TitleCol> cols = new ArrayList<>();

        TitleCol titleCol1 = new TitleCol("序号(必填)");
        TitleCol titleCol2 = new TitleCol("订单编号(必填)");
        TitleCol titleCol3 = new TitleCol("入库计划单号(必填)");
        TitleCol titleCol4 = new TitleCol("货物规格编码(必填)");
        TitleCol titleCol5 = new TitleCol("货物名称");
        TitleCol titleCol7 = new TitleCol("规格");
        TitleCol titleCol8 = new TitleCol("入库日期(必填)");
        TitleCol titleCol9 = new TitleCol("原发数量(必填)");
        TitleCol titleCol10 = new TitleCol("实际入库数量(必填)");
        TitleCol titleCol12 = new TitleCol("单价/元");


        cols.add(titleCol1);
        cols.add(titleCol2);
        cols.add(titleCol3);
        cols.add(titleCol4);
        cols.add(titleCol5);
        cols.add(titleCol7);
        cols.add(titleCol8);
        cols.add(titleCol9);
        cols.add(titleCol10);
        cols.add(titleCol12);

        titleRow.setCols(cols);
        return titleRow;
    }

    public DataRow getDataRow(){
        DataRow row = new DataRow();

        DataCol dataCol1 = new DataCol("idx");
        // 添加类
        List<ValidatorBean> listValidatorBean1 = new ArrayList<>();
        // 必填
        listValidatorBean1.add(getValidatorRequired());
        dataCol1.setListValidators(listValidatorBean1);

        DataCol dataCol2 = new DataCol("order_no");
        // 添加类
        List<ValidatorBean> listValidatorBean2 = new ArrayList<>();
        // 必填
        listValidatorBean2.add(getValidatorRequired());
        dataCol2.setListValidators(listValidatorBean2);

        DataCol dataCol3 = new DataCol("plan_code");
        // 添加类
        List<ValidatorBean> listValidatorBean3 = new ArrayList<>();
        // 必填
        listValidatorBean3.add(getValidatorRequired());
        dataCol3.setListValidators(listValidatorBean3);
//        dataCol3.setListValidators(listCommonValidatorBean);

        DataCol dataCol4 = new DataCol("sku_code");
        // 添加类
        List<ValidatorBean> listValidatorBean4 = new ArrayList<>();
        // 必填
        listValidatorBean4.add(getValidatorRequired());
        dataCol4.setListValidators(listValidatorBean4);

        DataCol dataCol5 = new DataCol("goods_name");

        DataCol dataCol7 = new DataCol("spec");

        DataCol dataCol8 = new DataCol("inbound_time");
        dataCol8.setConvertor(ExcelImportConvertorsConstants.CONVERTOR_DATE);
        // 添加类
        List<ValidatorBean> listValidatorBean8 = new ArrayList<>();
        // 必填
        listValidatorBean8.add(getValidatorRequired());
        // 日期check
        listValidatorBean8.add(getDateTimeValidator());
//        listValidatorBean8.add(getValidatorDatetimeRequired());
        dataCol8.setListValidators(listValidatorBean8);

        DataCol dataCol9 = new DataCol("primary_quantity");
        // 添加类
        List<ValidatorBean> listValidatorBean9 = new ArrayList<>();
        // 必填
        listValidatorBean9.add(getValidatorRequired());
        // 数值
        listValidatorBean9.add(getValidatorNumRequired());
        dataCol9.setListValidators(listValidatorBean9);

        DataCol dataCol10 = new DataCol("actual_weight");
        // 添加类
        List<ValidatorBean> listValidatorBean10 = new ArrayList<>();
        // 必填
        listValidatorBean10.add(getValidatorRequired());
        listValidatorBean10.add(getValidatorNumRequired());
        dataCol10.setListValidators(listValidatorBean10);

        DataCol dataCol12 = new DataCol("price");
        // 添加类
        List<ValidatorBean> listValidatorBean12 = new ArrayList<>();
        // 必填
//        listValidatorBean12.add(getValidatorRequired());
        listValidatorBean12.add(getValidatorNumRequired());
        dataCol12.setListValidators(listValidatorBean12);

        row.addDataCol(dataCol1);
        row.addDataCol(dataCol2);
        row.addDataCol(dataCol3);
        row.addDataCol(dataCol4);
        row.addDataCol(dataCol5);
        row.addDataCol(dataCol7);
        row.addDataCol(dataCol8);
        row.addDataCol(dataCol9);
        row.addDataCol(dataCol10);
        row.addDataCol(dataCol12);

        /**
         * 添加行级check
         */
        DataRowValidators dataRowValidators = new DataRowValidators();
        List<ValidatorBean> rows_validators = new ArrayList<>();
        // 入库时间校验
//        rows_validators.add(checkInboundTime_rowcheck());
        // 入库计划编号check
        rows_validators.add(checkBInPlan_rowcheck());
        // 规格编号check
        rows_validators.add(checkSku_rowcheck());
        // 订单编号check
        rows_validators.add(checkOrder_rowcheck());
        // 入库计划下物料check
        rows_validators.add(checkPlanSku_rowcheck());
        // 入库计划下订单编号check
        rows_validators.add(checkPlanOrder_rowcheck());
        // 入库计划状态check
        rows_validators.add(checkPlanStatus_rowcheck());
        dataRowValidators.setListValidators(rows_validators);
        row.setDataRowValidators(dataRowValidators);


        return row;
    }

    private ValidatorBean getDateTimeValidator() {
        ValidatorBean bean = new ValidatorBean();
        bean.setValidtorName(ExcelImportValidatorConstants.VALIDATOR_DATETIME);

        List<NameAndValue> param = new ArrayList<>();
        NameAndValue nameAndValue = new NameAndValue();
        nameAndValue.setName("dateFormat_json");
        nameAndValue.setValue("[\"yyyy/MM/dd\",\"yyyy-MM-dd\",\"yyyyMMdd\"]");
        param.add(nameAndValue);
        bean.setParam(param);
        return bean;
    }

    /**
     * 必须输入的校验
     * @return
     */
    private ValidatorBean getValidatorRequired(){
        ValidatorBean bean = new ValidatorBean();
        bean.setValidtorName(ExcelImportValidatorConstants.VALIDATOR_REQUIRED);

        List<NameAndValue> param = new ArrayList<>();

        bean.setParam(param);
        return bean;
    }

    /**
     * 必须输入数值的校验
     * @return
     */
    private ValidatorBean getValidatorNumRequired(){
        ValidatorBean bean = new ValidatorBean();
        bean.setValidtorName(ExcelImportValidatorConstants.VALIDATOR_MIN);

        List<NameAndValue> param = new ArrayList<>();
        NameAndValue nameAndValue = new NameAndValue();
        nameAndValue.setName("min");
        nameAndValue.setValue(0f);
        param.add(nameAndValue);

        bean.setParam(param);
        return bean;
    }

    /**
     * 反射check-入单单号
     * @return
     */
    private ValidatorBean checkBInPlan_rowcheck(){
        ValidatorBean bean = new ValidatorBean();
        bean.setValidtorName(ExcelImportValidatorConstants.VALIDATOR_REFLECTION);
        List<NameAndValue> param = new ArrayList<>();

        NameAndValue className = new NameAndValue();
        // 设置参数中的field名称
        className.setName("className");
        // 设置参数中的field的值
        className.setValue("com.xinyirun.scm.core.system.serviceimpl.business.in.BInServiceImpl");
        param.add(className);

        NameAndValue functionName = new NameAndValue();
        // 设置参数中的field名称
        functionName.setName("functionName");
        // 设置参数中的field的值
        functionName.setValue("checkBInPlan");
        param.add(functionName);
        NameAndValue errorMsg = new NameAndValue();
        errorMsg.setName("errorMsg");
        errorMsg.setValue("入库计划单号不存在");
        param.add(errorMsg);

        bean.setParam(param);

        return bean;
    }

    /**
     * 反射check-物料编号
     * @return
     */
    private ValidatorBean checkSku_rowcheck(){
        ValidatorBean bean = new ValidatorBean();
        bean.setValidtorName(ExcelImportValidatorConstants.VALIDATOR_REFLECTION);
        List<NameAndValue> param = new ArrayList<>();

        NameAndValue className = new NameAndValue();
        // 设置参数中的field名称
        className.setName("className");
        // 设置参数中的field的值
        className.setValue("com.xinyirun.scm.core.system.serviceimpl.business.in.BInServiceImpl");
        param.add(className);

        NameAndValue functionName = new NameAndValue();
        // 设置参数中的field名称
        functionName.setName("functionName");
        // 设置参数中的field的值
        functionName.setValue("checkSku");
        param.add(functionName);
        NameAndValue errorMsg = new NameAndValue();
        errorMsg.setName("errorMsg");
        errorMsg.setValue("商品不存在");
        param.add(errorMsg);

        bean.setParam(param);

        return bean;
    }

    /**
     * 反射check-订单编号
     * @return
     */
    private ValidatorBean checkOrder_rowcheck(){
        ValidatorBean bean = new ValidatorBean();
        bean.setValidtorName(ExcelImportValidatorConstants.VALIDATOR_REFLECTION);
        List<NameAndValue> param = new ArrayList<>();

        NameAndValue className = new NameAndValue();
        // 设置参数中的field名称
        className.setName("className");
        // 设置参数中的field的值
        className.setValue("com.xinyirun.scm.core.system.serviceimpl.business.in.BInServiceImpl");
        param.add(className);

        NameAndValue functionName = new NameAndValue();
        // 设置参数中的field名称
        functionName.setName("functionName");
        // 设置参数中的field的值
        functionName.setValue("checkOrder");
        param.add(functionName);
        NameAndValue errorMsg = new NameAndValue();
        errorMsg.setName("errorMsg");
        errorMsg.setValue("订单编号不存在");
        param.add(errorMsg);

        bean.setParam(param);

        return bean;
    }

    /**
     * 反射check-订单编号
     * @return
     */
    private ValidatorBean checkPlanSku_rowcheck(){
        ValidatorBean bean = new ValidatorBean();
        bean.setValidtorName(ExcelImportValidatorConstants.VALIDATOR_REFLECTION);
        List<NameAndValue> param = new ArrayList<>();

        NameAndValue className = new NameAndValue();
        // 设置参数中的field名称
        className.setName("className");
        // 设置参数中的field的值
        className.setValue("com.xinyirun.scm.core.system.serviceimpl.business.in.BInServiceImpl");
        param.add(className);

        NameAndValue functionName = new NameAndValue();
        // 设置参数中的field名称
        functionName.setName("functionName");
        // 设置参数中的field的值
        functionName.setValue("checkPlanSku");
        param.add(functionName);
        NameAndValue errorMsg = new NameAndValue();
        errorMsg.setName("errorMsg");
        errorMsg.setValue("入库计划下无该商品");
        param.add(errorMsg);

        bean.setParam(param);

        return bean;
    }

    /**
     * 反射check-入库计划下订单编号
     * @return
     */
    private ValidatorBean checkPlanOrder_rowcheck(){
        ValidatorBean bean = new ValidatorBean();
        bean.setValidtorName(ExcelImportValidatorConstants.VALIDATOR_REFLECTION);
        List<NameAndValue> param = new ArrayList<>();

        NameAndValue className = new NameAndValue();
        // 设置参数中的field名称
        className.setName("className");
        // 设置参数中的field的值
        className.setValue("com.xinyirun.scm.core.system.serviceimpl.business.in.BInServiceImpl");
        param.add(className);

        NameAndValue functionName = new NameAndValue();
        // 设置参数中的field名称
        functionName.setName("functionName");
        // 设置参数中的field的值
        functionName.setValue("checkPlanOrder");
        param.add(functionName);
        NameAndValue errorMsg = new NameAndValue();
        errorMsg.setName("errorMsg");
        errorMsg.setValue("订单编号下无该入库计划单号");
        param.add(errorMsg);

        bean.setParam(param);

        return bean;
    }

    /**
     * 反射check-入库计划状态
     * @return
     */
    private ValidatorBean checkPlanStatus_rowcheck(){
        ValidatorBean bean = new ValidatorBean();
        bean.setValidtorName(ExcelImportValidatorConstants.VALIDATOR_REFLECTION);
        List<NameAndValue> param = new ArrayList<>();

        NameAndValue className = new NameAndValue();
        // 设置参数中的field名称
        className.setName("className");
        // 设置参数中的field的值
        className.setValue("com.xinyirun.scm.core.system.serviceimpl.business.in.BInServiceImpl");
        param.add(className);

        NameAndValue functionName = new NameAndValue();
        // 设置参数中的field名称
        functionName.setName("functionName");
        // 设置参数中的field的值
        functionName.setValue("checkBInPlanStatus");
        param.add(functionName);
        NameAndValue errorMsg = new NameAndValue();
        errorMsg.setName("errorMsg");
        errorMsg.setValue("该入库计划为非审核通过状态");
        param.add(errorMsg);

        bean.setParam(param);

        return bean;
    }
//
//    /**
//     * 反射check-入库时间
//     * @return
//     */
//    private ValidatorBean checkInboundTime_rowcheck(){
//        ValidatorBean bean = new ValidatorBean();
//        bean.setValidtorName(ExcelImportValidatorConstants.VALIDATOR_REFLECTION);
//        List<NameAndValue> param = new ArrayList<>();
//
//        NameAndValue className = new NameAndValue();
//        // 设置参数中的field名称
//        className.setName("className");
//        // 设置参数中的field的值
//        className.setValue("com.xinyirun.scm.core.system.serviceimpl.business.in.BInServiceImpl");
//        param.add(className);
//
//        NameAndValue functionName = new NameAndValue();
//        // 设置参数中的field名称
//        functionName.setName("functionName");
//        // 设置参数中的field的值
//        functionName.setValue("checkInboundTime");
//        param.add(functionName);
//        NameAndValue errorMsg = new NameAndValue();
//        errorMsg.setName("errorMsg");
//        errorMsg.setValue("入库日期错误");
//        param.add(errorMsg);
//
//        bean.setParam(param);
//
//        return bean;
//    }
}
