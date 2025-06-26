package com.xinyirun.scm.common.enums;

/**
 * 操作日志 枚举类型
 */
public enum OperationEnum {
	ADD("ADD", "新增"),
	UPDATE("UPDATE", "更新"),
	DELETE("DELETE", "物理删除"),
	LOGIC_DELETE("LOGIC_DELETE", "逻辑删除"),
	DRAG_DROP("DRAG_DROP", "拖拽操作"),
	BATCH_UPDATE_INSERT_DELETE("BATCH_UPDATE_INSERT_DELETE", "批量操作")
	;

	public String getType() {
		if (this.equals(ADD)) {
			return "ADD";
		}
		if (this.equals(UPDATE)) {
			return "UPDATE";
		}
		if (this.equals(DELETE)) {
			return "DELETE";
		}
		if (this.equals(LOGIC_DELETE)) {
			return "LOGIC_DELETE";
		}
		return null;
	}


	private String code;

	private String name;

	OperationEnum(String code, String name) {
		this.code = code;
		this.name = name;
	}

	public String getCode() {
		return code;
	}

	public String getName(){
		return name;
	}

	public static OperationEnum getByCode(String code){
		for(OperationEnum type : values()){
			if (type.getName().equals(code)) {
				//获取指定的枚举
				return type;
			}
		}
		return null;
	}
}
