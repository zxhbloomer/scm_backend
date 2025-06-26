package com.xinyirun.scm.common.annotations;

import com.xinyirun.scm.common.enums.OperationEnum;
import com.xinyirun.scm.common.enums.ParameterEnum;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 具体的操作，日志中需要保存的内容
 * @author zxh
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface LogByCustomKeysAnnotion {
	/** 业务名 */
	String name();
	/** 操作类型 */
	OperationEnum type();
	/** 业务操作说明 */
	String oper_info();
	/** 表名 */
	String table_name();
	/** 需要记录的字段 */
	String[] cloums() default {};
	/** key所在参数的位置 */
	ParameterEnum key_position();
	/** key字段的值， */
	String keys() default "";
	String keys_sql() default "";
}
