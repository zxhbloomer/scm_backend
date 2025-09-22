//package com.xinyirun.scm.common.config.mongodb;
//
//import com.xinyirun.scm.common.convert.BigDecimalToDecimal128Converter;
//import com.xinyirun.scm.common.convert.Decimal128ToBigDecimalConverter;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.core.convert.converter.Converter;
//import org.springframework.data.mongodb.core.convert.MongoCustomConversions;
//
//import java.util.ArrayList;
//import java.util.List;
//
///**
// * @author Wang Qianfeng
// * @Description java BigDecimal 和 mongodb bigDecimal128 类型的转换
// * @date 2023/2/14 16:33
// */
//
//@Configuration
//public class MongoConvertConfig {
//
//    @Bean
//    public MongoCustomConversions mongoCustomConversions() {
//        List<Converter<?, ?>> converterList = new ArrayList<>();
//        converterList.add(new BigDecimalToDecimal128Converter());
//        converterList.add(new Decimal128ToBigDecimalConverter());
//        return new MongoCustomConversions(converterList);
//    }
//
////    @Bean
////    MongoTransactionManager transactionManager(MongoDatabaseFactory factory){
////        return new MongoTransactionManager(factory);
////    }
//}
