package com.xinyirun.scm.common.convert;

import org.bson.types.Decimal128;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.WritingConverter;

import java.math.BigDecimal;

/**
 * @author Wang Qianfeng
 * @Description java -> mongo BigDecimal -> BigDecimal128
 * @date 2023/2/14 16:24
 */

@WritingConverter
public class BigDecimalToDecimal128Converter implements Converter<BigDecimal, Decimal128> {

    @Override
    public Decimal128 convert(BigDecimal bigDecimal) {
        return new Decimal128(BigDecimal.valueOf(bigDecimal.doubleValue()));
    }
}
