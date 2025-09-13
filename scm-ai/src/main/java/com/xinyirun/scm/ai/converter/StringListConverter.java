/**
 * 字符串列表转换器，用于List<String>与逗号分隔字符串的相互转换
 */
package com.xinyirun.scm.ai.converter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.springframework.util.StringUtils;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class StringListConverter implements AttributeConverter<List<String>, String> {

    @Override
    public String convertToDatabaseColumn(List<String> list) {
        if (list == null) {
            return "";
        }
        Iterator<String> iterator = list.iterator();  
        while(iterator.hasNext()){  
            String str = iterator.next();  
            if(!StringUtils.hasText(str)){  
                iterator.remove();
            }  
        }
        return String.join(",", list);
    }

    @Override
    public List<String> convertToEntityAttribute(String joined) {
        if (joined == null) {
            return new ArrayList<>();
        }
        return new ArrayList<>(Arrays.asList(joined.split(",")));
    }

}
