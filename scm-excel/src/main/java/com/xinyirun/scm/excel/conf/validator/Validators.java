package com.xinyirun.scm.excel.conf.validator;

import com.xinyirun.scm.excel.conf.constant.ExcelImportValidatorConstants;
import com.xinyirun.scm.excel.upload.SystemExcelException;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * validators 类
 *
 * @author zxh
 * @date 2019/1/11
 */
public class Validators {

    private static Validators instance = null;

    private Map<String, Class> validatorMap = new HashMap<String, Class>();

    private Validators() {
        initDefaultValidators();
    }

    public static Validators instance() {
        if (instance == null) {
            instance = new Validators();
        }
        return instance;
    }

    /**
     * 初始化所有的validator
     */
    private void initDefaultValidators() {
        validatorMap.put(ExcelImportValidatorConstants.VALIDATOR_NUM, NumValidator.class);
        validatorMap.put(ExcelImportValidatorConstants.VALIDATOR_MIN, MinValidator.class);
        validatorMap.put(ExcelImportValidatorConstants.VALIDATOR_MAX, MaxValidator.class);
        validatorMap.put(ExcelImportValidatorConstants.VALIDATOR_MIN_LENGTH, MinLengthValidator.class);
        validatorMap.put(ExcelImportValidatorConstants.VALIDATOR_MAX_LENGTH, MaxLengthValidator.class);
        validatorMap.put(ExcelImportValidatorConstants.VALIDATOR_RANGE_LENGTH, RangeLengthValidator.class);
        validatorMap.put(ExcelImportValidatorConstants.VALIDATOR_RANGE, RangeValidator.class);
        validatorMap.put(ExcelImportValidatorConstants.VALIDATOR_MOBILE, MobileValidator.class);
        validatorMap.put(ExcelImportValidatorConstants.VALIDATOR_CHINESE, ChineseValidator.class);
        validatorMap.put(ExcelImportValidatorConstants.VALIDATOR_EMAIL, EmailValidator.class);
        validatorMap.put(ExcelImportValidatorConstants.VALIDATOR_REQUIRED, RequiredValidator.class);
        validatorMap.put(ExcelImportValidatorConstants.VALIDATOR_REGEX, RegexValidator.class);
        validatorMap.put(ExcelImportValidatorConstants.VALIDATOR_DATETIME, DateTimeValidator.class);
        validatorMap.put(ExcelImportValidatorConstants.VALIDATOR_REFLECTION, ReflectionValidtor.class);
    }

    /**
     * 获取相应的validator
     * @param name
     * @return
     */
    public Validator getValidator(String name) {
        if (!validatorMap.containsKey(name)) {
            throw new IllegalArgumentException(String.format("Validator:%s不存在", name));
        }
        try {
            return (Validator) validatorMap.get(name).newInstance();
        } catch (InstantiationException e) {
            throw new SystemExcelException(e);
        } catch (IllegalAccessException e) {
            throw new SystemExcelException(e);
        }
    }

    /**
     * 注册validator到map中
     * @param name
     * @param clasz
     */
    public void registValidator(String name, Class clasz) {
        if (validatorMap.containsKey(name)) {
            throw new IllegalArgumentException(String.format("Validator:%s已存在", name));
        }
        validatorMap.put(name, clasz);
    }
}
