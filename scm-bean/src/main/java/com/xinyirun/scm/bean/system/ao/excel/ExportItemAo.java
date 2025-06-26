package com.xinyirun.scm.bean.system.ao.excel;

import com.xinyirun.scm.bean.system.config.base.BaseVo;

public class ExportItemAo extends BaseVo {

	private static final long serialVersionUID = -2940955578158684124L;

	private String field; // 属性名
	private String display; // 显示名
	private short width; // 宽度
	private String convert;
	private short color;
	private String replace;

	public String getField() {
		return field;
	}

	public ExportItemAo setField(String field) {
		this.field = field;
		return this;
	}

	public String getDisplay() {
		return display;
	}

	public ExportItemAo setDisplay(String display) {
		this.display = display;
		return this;
	}

	public short getWidth() {
		return width;
	}

	public ExportItemAo setWidth(short width) {
		this.width = width;
		return this;
	}

	public String getConvert() {
		return convert;
	}

	public ExportItemAo setConvert(String convert) {
		this.convert = convert;
		return this;
	}

	public short getColor() {
		return color;
	}

	public ExportItemAo setColor(short color) {
		this.color = color;
		return this;
	}

	public String getReplace() {
		return replace;
	}

	public ExportItemAo setReplace(String replace) {
		this.replace = replace;
		return this;
	}
}
