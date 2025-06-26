package com.xinyirun.scm.common.utils;

import com.alibaba.fastjson2.JSON;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * json与对象转换工具类
 * @author CZH
 */
@Slf4j
public class JsonConvertUtil {

	/**
	 * json转实体对象
	 * @param jsonName
	 * @param tClass
	 * @return
	 */
	public static <T> T json2Obj(String jsonName, Class<T> tClass) {
		try {
			return JSON.parseObject(jsonName, tClass);
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * json转集合对象
	 * @param jsonName
	 * @param tClass
	 * @return
	 */
	public static <T> List<T> json2List(String jsonName, Class<T> tClass) {
		try {
			InputStream jsonInStream = JsonConvertUtil.class.getClassLoader().getResourceAsStream(jsonName);
			String jsonStr = convertStream2Json(jsonInStream);
			return JSON.parseArray(jsonStr, tClass);
		} catch (Exception e) {
			return null;
		}
	}

	public static String convertStream2Json(InputStream inputStream) {
		String jsonStr = "";
		// ByteArrayOutputStream相当于内存输出流
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024];
		int len = 0;
		// 将输入流转移到内存输出流中
		try {
			while ((len = inputStream.read(buffer, 0, buffer.length)) != -1) {
				out.write(buffer, 0, len);
			}
			// 将内存流转换为字符串
			jsonStr = new String(out.toByteArray(), "UTF-8");
		} catch (IOException e) {
			log.error("convertStream2Json error:", e);
		}
		return jsonStr;
	}
}
