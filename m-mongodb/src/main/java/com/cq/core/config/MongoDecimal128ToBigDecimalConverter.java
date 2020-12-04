package com.cq.core.config;

import org.bson.types.Decimal128;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;

import java.math.BigDecimal;

/**
 * Created by lin on 2020-09-28.
 */
@ReadingConverter
public class MongoDecimal128ToBigDecimalConverter implements Converter<Decimal128, BigDecimal> {

    @Override
    public BigDecimal convert(Decimal128 source) {
        return source.bigDecimalValue();
    }
}
