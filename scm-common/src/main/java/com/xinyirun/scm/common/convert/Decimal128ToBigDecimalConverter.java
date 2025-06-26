package com.xinyirun.scm.common.convert;

import org.bson.types.Decimal128;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;

import java.math.BigDecimal;

/**
 * @author Wang Qianfeng
 * @Description mongo--->java  即Decimal128变为BigDecimal的转换器
 * @date 2023/2/14 16:30
 */

@ReadingConverter
public class Decimal128ToBigDecimalConverter implements Converter<Decimal128, BigDecimal> {

    @Override
    public BigDecimal convert(Decimal128 decimal128) {
        return decimal128.bigDecimalValue();
    }
}
