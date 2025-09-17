package com.xinyirunscm.scm.clickhouse.entity;

import java.time.LocalDateTime;

/**
 * ClickHouse 数据变更日志实体类
 * 
 * @author SCM System
 * @since 1.0.39
 */
public class ClickHouseLogEntity {

    /**
     * 日志ID
     */
    private String log_id;

    /**
     * 租户ID
     */
    private String tenant_id;

    /**
     * 表名
     */
    private String table_name;

    /**
     * 操作类型：INSERT、UPDATE、DELETE
     */
    private String operation_type;

    /**
     * 记录ID
     */
    private String record_id;

    /**
     * 变更时间
     */
    private LocalDateTime change_time;

    /**
     * 用户ID
     */
    private Long user_id;

    /**
     * 用户名
     */
    private String user_name;

    /**
     * 请求ID
     */
    private String request_id;

    /**
     * 订单编号
     */
    private String order_code;

    /**
     * 变更前数据（JSON格式）
     */
    private String before_data;

    /**
     * 变更后数据（JSON格式）
     */
    private String after_data;

    /**
     * 变更字段列表（JSON格式）
     */
    private String changed_fields;

    /**
     * IP地址
     */
    private String ip_address;

    /**
     * 用户代理
     */
    private String user_agent;

    /**
     * 备注信息
     */
    private String remark;

    /**
     * 创建时间
     */
    private LocalDateTime create_time;

    public String getLog_id() {
        return log_id;
    }

    public void setLog_id(String log_id) {
        this.log_id = log_id;
    }

    public String getTenant_id() {
        return tenant_id;
    }

    public void setTenant_id(String tenant_id) {
        this.tenant_id = tenant_id;
    }

    public String getTable_name() {
        return table_name;
    }

    public void setTable_name(String table_name) {
        this.table_name = table_name;
    }

    public String getOperation_type() {
        return operation_type;
    }

    public void setOperation_type(String operation_type) {
        this.operation_type = operation_type;
    }

    public String getRecord_id() {
        return record_id;
    }

    public void setRecord_id(String record_id) {
        this.record_id = record_id;
    }

    public LocalDateTime getChange_time() {
        return change_time;
    }

    public void setChange_time(LocalDateTime change_time) {
        this.change_time = change_time;
    }

    public Long getUser_id() {
        return user_id;
    }

    public void setUser_id(Long user_id) {
        this.user_id = user_id;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getRequestId() {
        return request_id;
    }

    public void setRequest_id(String request_id) {
        this.request_id = request_id;
    }

    public String getOrderCode() {
        return order_code;
    }

    public void setOrderCode(String order_code) {
        this.order_code = order_code;
    }

    public String getBefore_data() {
        return before_data;
    }

    public void setBefore_data(String before_data) {
        this.before_data = before_data;
    }

    public String getAfter_data() {
        return after_data;
    }

    public void setAfter_data(String after_data) {
        this.after_data = after_data;
    }

    public String getChanged_fields() {
        return changed_fields;
    }

    public void setChanged_fields(String changed_fields) {
        this.changed_fields = changed_fields;
    }

    public String getIp_address() {
        return ip_address;
    }

    public void setIp_address(String ip_address) {
        this.ip_address = ip_address;
    }

    public String getUser_agent() {
        return user_agent;
    }

    public void setUser_agent(String user_agent) {
        this.user_agent = user_agent;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public LocalDateTime getCreate_time() {
        return create_time;
    }

    public void setCreate_time(LocalDateTime create_time) {
        this.create_time = create_time;
    }
}