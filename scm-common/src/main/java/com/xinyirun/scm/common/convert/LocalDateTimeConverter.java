//package com.xinyirun.scm.common.convert;
//
//
//import cn.idev.excel.converters.Converter;
//import cn.idev.excel.enums.CellDataTypeEnum;
//import cn.idev.excel.metadata.CellData;
//import cn.idev.excel.metadata.GlobalConfiguration;
//import cn.idev.excel.metadata.property.ExcelContentProperty;
//
//import java.time.LocalDateTime;
//import java.time.format.DateTimeFormatter;
//
//public class LocalDateTimeConverter implements Converter<LocalDateTime> {
//
//    @Override
//    public Class<LocalDateTime> supportJavaTypeKey() {
//        return LocalDateTime.class;
//    }
//
//    @Override
//    public CellDataTypeEnum supportExcelTypeKey() {
//        return CellDataTypeEnum.STRING;
//    }
//
//    @Override
//    public LocalDateTime convertToJavaData(CellData cellData, ExcelContentProperty contentProperty,
//                                           GlobalConfiguration globalConfiguration) {
//        return LocalDateTime.parse(cellData.getStringValue(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
//    }
//
//    @Override
//    public CellData<String> convertToExcelData(LocalDateTime value, ExcelContentProperty contentProperty,
//                                               GlobalConfiguration globalConfiguration) {
//        return new CellData<>(value.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
//    }
//}
