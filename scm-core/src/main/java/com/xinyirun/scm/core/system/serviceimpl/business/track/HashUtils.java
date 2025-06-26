package com.xinyirun.scm.core.system.serviceimpl.business.track;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.*;

/**
 * hashMap  or Hashtable  的常用类
 * 
 * @author zxxiang
 *
 */
public class HashUtils {
	
	static final Logger logger  = LoggerFactory.getLogger(HashUtils.class);
	
	/**
	 * 获取String值
	 * 
	 * @param rec
	 * @param key
	 */
	public static String getStringValue(Map rec,String key){		
		return rec==null || rec.get(key) == null ? null : String.valueOf(rec.get(key));		
	}
	
	/**
	 * 获取Integer值
	 * 
	 * @param rec
	 * @param key
	 */
	public static Long getLongValue(Map rec,String key){
		String strVal =  rec.get(key) == null ? null : rec.get(key).toString();

		if( StringUtils.isEmpty(strVal))
			return null;

		if(strVal.indexOf(".") == -1)
			return Long.valueOf(strVal);
		else
			return getBigDecimal(rec,key).longValue();
	}
	
	/**
	 * 获取Integer值
	 * 
	 * @param rec
	 * @param key
	 */
	public static Integer getIntegerValue(Map rec,String key){	
		String strVal =  rec.get(key) == null ? null : rec.get(key).toString();
		return StringUtils.isEmpty(strVal)? null : new BigDecimal(strVal).intValue();		
	}
	
	/**
	 * 获取Double值
	 * 
	 * @param rec
	 * @param key
	 */
	@SuppressWarnings("unchecked")
	public static Double getDoubleValue(Map rec,String key){	
		String strVal =  rec.get(key) == null ? null : rec.get(key).toString();
		return StringUtils.isEmpty(strVal)? null : new BigDecimal(strVal).doubleValue();		
	}
	
	/**
	 * 两个Map，在某些字段是否相等
	 * 
	 * @param ht1 
	 * @param ht2
	 * @param keys
	 * @return
	 */
	public static boolean equalsWith(Map ht1, Map ht2, String... keys) {
		
		boolean eq = true;
		for(String key : keys){
			Object o1 = ht1.get(key);
			Object o2 = ht2.get(key);
			
			if((o1 == null && o1 != o2) ||(o1.equals(o2)==false)){
				eq = false;
				break;
			}
		}
		
		return eq;		
	}

	/**
	 * 通过数组快速创建参数Map (有序的LinkedHashMap)
	 * 
	 * @param params key1,value1,key2,value2,key3,value3 ...
	 * @return map
	 */
	public static Map getMap(Object... params) {
		LinkedHashMap<String, Object> map = new LinkedHashMap<String, Object>();
		if(params.length % 2 !=0){
			throw new RuntimeException("键值对必须为偶数个");
		}
		
		for(int i=0;i<params.length;){
			map.put(params[i].toString(), params[i+1]);
			i+=2;
		}
		return map;
	}

	/**
	 * 得到时间值
	 * 
	 * @param map
	 * @param key
	 * @return
	* @version 1.0
	 */
	public static Date getDateValue(Map<String, Object> map, String key) {
		Date v =  map.get(key) == null ? null : (Date)map.get(key);
		return v;
	}
	
	/**
	 * 查看列表中，cell值
	 * 
	 * @param list
	 * @param key
	 * @return
	 * @version 1.0
	 */
	public static Set<Object> distinctList(List<Map<String,Object>> list,String key,Comparator<Object> objCpt){
		Set<Object> objSet = new HashSet();
		for(Map<String,Object> map : list){
			if(map.get(key)!=null){
				Object o = map.get(key);
				for(Object obj : objSet){
					if(objCpt.compare(obj, o) !=0){
						objSet.add(o);
					}
				}
			}
		}
		
		return objSet;
	}
	
	/**
	 * 统计列
	 * 
	 * @param list
	 * @return
	 * @version 1.0
	 */
	public static BigDecimal sumList(List<Map<String,Object>> list,String key){
		BigDecimal total = BigDecimal.ZERO;
		for(Map<String,Object> map : list){
			if(map.get(key)!=null){
				BigDecimal dct = getBigDecimal(map, key);
				total = total.add(dct);
			}
		}
		return total;
	}

	/**
	 * 获得最高项目
	 * 
	 * @param map
	 * @param amtField
	 * @return
	 * @version 1.0
	 */
	public static BigDecimal getBigDecimal(Map<String, Object> map,
			String amtField) {
		String bgc = HashUtils.getStringValue(map, amtField);
		if(StringUtils.isNotEmpty(bgc))
			return new BigDecimal(bgc);
		
		return BigDecimal.ZERO;
	}

	/**
	 * 得到布尔值
	 * @param map
	 * @param field
	 * @return
	 */
	public static Boolean getBoolean(Map<String, Object> map,String field) {
		String strV = HashUtils.getStringValue(map, field);
		if(StringUtils.isNotEmpty(strV))
			return Boolean.valueOf(strV);

		return null;
	}
}

