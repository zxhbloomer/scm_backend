package com.xinyirun.scm.excel.conf.validator;

/**
 *
 * validator工具类
 *
 * @author zxh
 * @date 2019/1/11
 */
public class ValidatorUtil {

    public static Validator getValidator(String name) {
        return Validators.instance().getValidator(name);
    }

    public static void registValidator(String name, Class clasz) {
        Validators.instance().registValidator(name, clasz);
    }
}
