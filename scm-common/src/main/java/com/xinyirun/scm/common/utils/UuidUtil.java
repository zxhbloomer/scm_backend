package com.xinyirun.scm.common.utils;

import java.util.UUID;

public class UuidUtil {
	/**
	 * @return 32位的UUID
	 */
	public static String randomUUID() {
		String uuid = UUID.randomUUID().toString();
		uuid = uuid.replaceAll("-", "");
		
		return uuid;
	}
}
