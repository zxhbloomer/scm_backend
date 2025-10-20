package com.xinyirun.scm.framework.config.mybatis.plugin.autofill;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.xinyirun.scm.bean.utils.security.SecurityUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;

import java.time.LocalDateTime;

/**
 * @author zxh
 */
@Slf4j
public class MyBatisAutoFillHandel implements MetaObjectHandler {

    /**
     * 新增的时候自动填充
     * @param metaObject
     */
    @Override
    public void insertFill(MetaObject metaObject) {
        Object skipAutoFill = getFieldValByName("skipAutoFill", metaObject);
        if (Boolean.TRUE.equals(skipAutoFill)) {
            return; // 跳过自动填充
        }

        log.info(" ....新增的时候自动填充 ....");
        this.setFieldValByNameMy("c_time", LocalDateTime.now(), metaObject);
        this.setFieldValByNameMy("cTime", LocalDateTime.now(), metaObject);
        this.setFieldValByNameMy("u_time", LocalDateTime.now(), metaObject);
        this.setFieldValByNameMy("uTime", LocalDateTime.now(), metaObject);
        this.setFieldValByNameMy("dbversion", 0, metaObject);

        try {
            this.setFieldValByNameMy("c_id", SecurityUtil.getUpdateUser_id(), metaObject);
            this.setFieldValByNameMy("cId", SecurityUtil.getUpdateUser_id(), metaObject);
            this.setFieldValByNameMy("u_id", SecurityUtil.getUpdateUser_id(), metaObject);
            this.setFieldValByNameMy("uId", SecurityUtil.getUpdateUser_id(), metaObject);
        } catch (Exception e) {
            log.error("自动填充更新c_id，u_id出错，需要在代码中set相应的值");
        }
        // 默认未删除
        if (this.getFieldValByName("is_del",metaObject) == null) {
            this.setFieldValByNameMy("is_del", false, metaObject);
        }
        // 默认未启用 是否启用(1:true-已启用,0:false-已禁用)
        if (this.getFieldValByName("is_enable",metaObject) == null) {
            this.setFieldValByNameMy("is_enable", false, metaObject);
        }
        if (this.getFieldValByName("enable",metaObject) == null) {
            this.setFieldValByNameMy("enable", false, metaObject);
        }
    }

    /**
     * 更新的时候自动填充
     * @param metaObject
     */
    @Override
    public void updateFill(MetaObject metaObject) {
        Object skipAutoFill = getFieldValByName("skipAutoFill", metaObject);
        if (Boolean.TRUE.equals(skipAutoFill)) {
            return; // 跳过自动填充
        }

        log.info(" ....更新的时候自动填充 ....");
        this.setFieldValByName("u_time", LocalDateTime.now(), metaObject);
        this.setFieldValByName("u_dt", LocalDateTime.now(), metaObject);
        this.setFieldValByName("u_id", SecurityUtil.getUpdateUser_id(), metaObject);
    }

    private void setFieldValByNameMy(String fieldName, Object fieldVal, MetaObject metaObject){
        try {
            this.setFieldValByName(fieldName, fieldVal, metaObject);
        } catch (Exception e) {
            log.error("自动填充未找到fieldName：" +fieldName);
        }
    }
}
