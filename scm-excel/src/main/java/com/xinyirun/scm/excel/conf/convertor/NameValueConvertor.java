package com.xinyirun.scm.excel.conf.convertor;

import com.xinyirun.scm.excel.upload.SystemExcelException;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author zxh
 * @date 2019/8/10
 */
public abstract class NameValueConvertor extends BaseConvertor {

    protected Map<String, String> nvMap = new HashMap<String, String>();

    protected abstract void initNameValues();

//    @Override
//    public String doConvert(Object input) {
//        if (!nvMap.containsKey(input)) {
//            throw new SystemExcelException(String.format("无效名：%s", input));
//        }
//        return nvMap.get(input);
//    }

    @Override
    public Object doConvertToType(String input) {
        for (String key : nvMap.keySet()
                ) {
            if (nvMap.get(key).equals(input)) {
                return key;
            }
        }
        throw new SystemExcelException(String.format("无效值：%s", input));
    }
}
