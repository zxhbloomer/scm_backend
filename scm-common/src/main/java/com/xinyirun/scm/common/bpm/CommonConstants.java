
package com.xinyirun.scm.common.bpm;

/**
 */
public interface CommonConstants {
	/** {@code 500 Server Error} (HTTP/1.0 - RFC 1945) */
	Integer SC_INTERNAL_SERVER_ERROR_500 = 500;
	/** {@code 200 OK} (HTTP/1.0 - RFC 1945) */
	Integer SC_OK_200 = 200;

	String FORM_VAR="formData";
	String PROCESS_STATUS="processStatus";
	String START_USER_INFO="startUser";
	String INITIATOR_ID="initiatorId";
	String BUSINESS_STATUS_ZERO ="0"; //正在处理
	String BUSINESS_STATUS_ONE ="1";//撤销
	String BUSINESS_STATUS_TWO ="2";//已完成
	String BUSINESS_STATUS_THREE ="3";//驳回


}
