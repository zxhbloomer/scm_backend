package com.xinyirun.scm.excel.bean.importconfig.template.title;

import com.alibaba.fastjson2.annotation.JSONField;
import lombok.Getter;
import lombok.Setter;

/**
 * 标题模板：列
 * @author zhangxh
 */
public class TitleCol {

    /**
     * 列名
     */
    @Getter @Setter
	@JSONField
	private String title;
    /**
     * 列跨度
     */
	@Getter @Setter
	@JSONField
	private int colSpan = 1;

	/**
	 * 构造函数
	 * @param title
	 */
	public TitleCol(String title) {
		this(title, 1);
	}

	/**
	 * 构造函数
	 * @param title
	 * @param colSpan
	 */
	public TitleCol(String title, int colSpan) {
		this.colSpan = colSpan;
		this.title = title;
	}
}
