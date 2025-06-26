package com.xinyirun.scm.common.convert.excel;


import cn.idev.excel.converters.Converter;
import cn.idev.excel.metadata.GlobalConfiguration;
import cn.idev.excel.metadata.data.WriteCellData;
import cn.idev.excel.metadata.property.ExcelContentProperty;

/**
 * @author Wang Qianfeng
 * @Description 验车状态转换
 * @date 2023/2/20 14:08
 */
public class ValidateVehicleConvertor implements Converter<String> {

    //导出数据到excel
    @Override
    public WriteCellData<?> convertToExcelData(String value, ExcelContentProperty contentProperty, GlobalConfiguration globalConfiguration) throws Exception {
        return new WriteCellData<>("1".equals(value) ? "验车通过" : "验车失败");
    }

}
