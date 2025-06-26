package com.xinyirun.scm.excel.conf.convertor;

/**
 *
 * @author zxh
 * @date 2019/8/10
 */
public class ConvertorUtil {

//    public static String convert(Object input, String convertor) {
//        return Convertors.instance().getConvertor(convertor).convert(input);
//    }

    public static Object convertToType(String input, String convertor) {
        return Convertors.instance().getConvertor(convertor).convertToType(input);
    }

    public static void registConvertor(String name, Convertor convertor) {
        Convertors.instance().registConvertor(name, convertor);
    }
}
