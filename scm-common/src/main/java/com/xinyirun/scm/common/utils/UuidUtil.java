package com.xinyirun.scm.common.utils;

import java.util.UUID;

/**
 * UUID 工具类
 * 用于生成32字符无连字符的UUID
 *
 * <p>标准UUID格式（36字符）: 550e8400-e29b-41d4-a716-446655440000</p>
 * <p>短UUID格式（32字符）: 550e8400e29b41d4a716446655440000</p>
 *
 * @author SCM AI Team
 * @since 2025-10-02
 */
public class UuidUtil {

	/**
	 * 生成32字符无连字符的UUID
	 *
	 * <p>该方法生成标准的UUID v4（随机UUID），然后移除连字符</p>
	 * <p>生成的UUID格式示例: 550e8400e29b41d4a716446655440000</p>
	 *
	 * @return 32字符的UUID字符串（无连字符）
	 */
	public static String createShort() {
		return UUID.randomUUID().toString().replace("-", "");
	}

	/**
	 * 生成32位的UUID（向后兼容方法）
	 *
	 * @deprecated 使用 {@link #createShort()} 替代
	 * @return 32位的UUID
	 */
	@Deprecated
	public static String randomUUID() {
		return createShort();
	}
}
