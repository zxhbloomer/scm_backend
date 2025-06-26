package com.xinyirun.scm.excel.bean.importconfig.template.data;

import com.alibaba.fastjson2.annotation.JSONField;
import com.xinyirun.scm.excel.bean.importconfig.template.validator.NameAndValue;
import com.xinyirun.scm.excel.bean.importconfig.template.validator.ValidatorBean;
import com.xinyirun.scm.excel.conf.validator.ColValidateResult;
import com.xinyirun.scm.excel.conf.validator.Validator;
import lombok.*;
import org.apache.poi.ss.formula.functions.T;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import static org.joor.Reflect.on;

/**
 * excel列模板bean
 * @author zxh
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class DataCol implements Serializable {

    private static final long serialVersionUID = 1246107721629872424L;

    /**
     * 列名
     */
    @JSONField
    private String name;
    /**
     * 列号
     */
    @JSONField
    private int index;

    /**
     * 转换类
     */
    @JSONField
    private String convertor;

    @JSONField
    private List<ValidatorBean> listValidators;

    /**
     * check类
     */
    @JSONField(serialize = false)
    private List<Validator> validators = new ArrayList<Validator>();

    /**
     * 构造函数
     * @param name
     */
    public DataCol(String name) {
        this.name = name;
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
     * @param input
     * @return
     */
    public <T> ColValidateResult validate(String input, T rowData) {
        ColValidateResult result = new ColValidateResult();
        result.setDataCol(this);
//        for (Validator validator : validators) {
//            if (!validator.validate(input)) {
//                result.setErrorMsg(validator.getErrorMsg());
//                break;
//            }
//        }
        for (int i = 0; i < validators.size(); i++) {
            Validator validator = validators.get(i);
            for (NameAndValue nv : listValidators.get(i).getParam()) {
                // 反射validate 设置field值
                on(validator).set(nv.getName(),nv.getValue());
            }
            if (!validator.validate(input, rowData)) {
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
    public boolean hasValidator() {
        return !validators.isEmpty();
    }
}
