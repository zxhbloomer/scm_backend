package com.xinyirun.scm.excel.bean.importconfig.template.data;

import com.alibaba.fastjson2.annotation.JSONField;
import com.xinyirun.scm.excel.bean.importconfig.template.data.row.DataRowValidators;
import com.xinyirun.scm.excel.bean.importconfig.template.validator.NameAndValue;
import com.xinyirun.scm.excel.conf.validator.ColValidateResult;
import com.xinyirun.scm.excel.conf.validator.Validator;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import static org.joor.Reflect.on;

/**
 * excel行模板类
 * @author zxh
 */
@AllArgsConstructor
@NoArgsConstructor
public class DataRow implements Serializable {

    private static final long serialVersionUID = -3512002550272910844L;

    /**
     * 列数据
     */
    @Getter
    @Setter
    @JSONField
    private List<DataCol> dataCols = new ArrayList<DataCol>();

    /**
     * 行级别的数据check
     */
    @Getter
    @Setter
    @JSONField
    private DataRowValidators dataRowValidators;

    /**
     * check类
     */
    @JSONField(serialize = false)
    private List<Validator> validators = new ArrayList<Validator>();

    /**
     * 添加列名
     * @param names
     */
    public void addDataCol(String... names) {
        for (String name : names) {
            addDataCol(new DataCol(name));
        }
    }

    /**
     * 添加列名
     * @param dataCol
     */
    public void addDataCol(DataCol dataCol) {
        dataCol.setIndex(dataCols.size());
        dataCols.add(dataCol);
    }

    /**
     * 列数
     * @return
     */
    public int colSize() {
        return dataCols.size();
    }

    /**
     * 添加check
     * @param validator
     */
    public void addValidator(Validator validator) {
        validators.add(validator);
    }

    /**
     * 验证
     */
    public <T> ColValidateResult validate(T rowData, ArrayList ... lists) {
        ColValidateResult result = new ColValidateResult();
//        result.setDataCol(this);

        for (int i = 0; i < validators.size(); i++) {
            Validator validator = validators.get(i);
            for (NameAndValue nv : dataRowValidators.getListValidators().get(i).getParam()) {
                // 反射validate 设置field值
                on(validator).set(nv.getName(),nv.getValue());
            }
            if (!validator.validate("", rowData, lists)) {
                result.setErrorMsg(validator.getErrorMsg());
                break;
            }
        }

        return result;
    }

    /**
     * 查看是否包含验证
     * @return
     */
    public boolean hasRowValidator() {
        return !validators.isEmpty();
    }
}
