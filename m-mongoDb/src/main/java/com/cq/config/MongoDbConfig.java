package com.cq.config;

import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSBuckets;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Configuration
public class MongoDbConfig {
    @Autowired
    private MongoDbFactory mongoDbFactory;

////    @Bean
////    public MappingMongoConverter mappingMongoConverter(MongoDbFactory factory, MongoMappingContext context, BeanFactory beanFactory) {
////        DbRefResolver dbRefResolver = new DefaultDbRefResolver(factory);
////        MappingMongoConverter mappingConverter = new MappingMongoConverter(dbRefResolver, context);
////        try {
////            mappingConverter.setCustomConversions(beanFactory.getBean(CustomConversions.class));
////        } catch (NoSuchBeanDefinitionException ignore) {
////        }
////
////        // Don't save _class to mongo
////        mappingConverter.setTypeMapper(new DefaultMongoTypeMapper(null));
////        return mappingConverter;
////    }
//
//    /**
//     * open Transaction
//     */
//    @Bean
//    MongoTransactionManager mongoTransactionManager(MongoDbFactory factory){
//        return new MongoTransactionManager(factory);
//    }

    @Bean
    public MongoCustomConversions mongoCustomConversions() {
        List<Converter<?, ?>> converterList = new ArrayList<>();
        converterList.add(new BigDecimalToMongoDecimal128Converter());
        converterList.add(new MongoDecimal128ToBigDecimalConverter());
        return new MongoCustomConversions(converterList);
    }

    @Bean
    public GridFSBucket getGridFSBuckets() {
        return GridFSBuckets.create(mongoDbFactory.getDb());
    }
}