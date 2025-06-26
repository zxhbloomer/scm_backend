package com.xinyirun.scm.common.enums;

/**
 * 操作日志 枚举类型
 */
public enum ParameterEnum {
	FIRST(0, "第一个参数"),
	SECOND(1, "第二个参数"),
	THIRD(2, "第三个参数"),
	FOURTH(3, "第四个参数"),
	FIFTH(4, "第五个参数"),
	SIXTH(5, "第六个参数"),
	SEVENTH(6, "第七个参数");

	private int code;

	private String name;

	ParameterEnum(int code, String name) {
		this.code = code;
		this.name = name;
	}

	public int getCode() {
		return code;
	}

	public String getName(){
		return name;
	}

	public static ParameterEnum getByCode(String code){
		for(ParameterEnum type : values()){
			if (type.getName().equals(code)) {
				//获取指定的枚举
				return type;
			}
		}
		return null;
	}
}
