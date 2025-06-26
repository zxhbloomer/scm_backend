package com.xinyirun.scm.excel.conf.validator;

import com.xinyirun.scm.common.utils.reflection.ReflectionUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class ReflectionValidtor extends Validator {
    private String className;
    private String functionName;
    private String parameterClass;
    private String parameter;

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getFunctionName() {
        return functionName;
    }

    public void setFunctionName(String functionName) {
        this.functionName = functionName;
    }

    public String getParameterClass() {
        return parameterClass;
    }

    public void setParameterClass(String parameterClass) {
        this.parameterClass = parameterClass;
    }

    public String getParameter() {
        return parameter;
    }

    public void setParameter(String parameter) {
        this.parameter = parameter;
    }


    @Override
    public <T> boolean validate(String input, T rowData, ArrayList ... lists) {
        boolean rtn = false;
        try {
            rtn = ReflectionUtil.invoke(className, functionName, rowData, lists);
        } catch (Exception e) {
            log.error("本行校验出错：", e);
            this.errorMsg = "本行校验出错：" + e.getMessage();
        } finally {
            return rtn;
        }
    }
}
