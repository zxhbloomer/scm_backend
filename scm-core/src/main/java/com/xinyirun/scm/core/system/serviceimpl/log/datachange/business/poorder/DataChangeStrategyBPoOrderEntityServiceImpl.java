package com.xinyirun.scm.core.system.serviceimpl.log.datachange.business.poorder;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.JSONWriter;
import com.xinyirun.scm.bean.entity.business.po.poorder.BPoOrderEntity;
import com.xinyirun.scm.bean.system.vo.master.user.MStaffVo;
import com.xinyirun.scm.bean.system.vo.mongo.log.SLogDataChangeDetailVo;
import com.xinyirun.scm.bean.system.vo.sys.log.datachange.SDataChangeLogDetailVo;
import com.xinyirun.scm.bean.utils.annotation.AnnotationUtil;
import com.xinyirun.scm.core.system.mapper.business.po.poorder.BPoOrderMapper;
import com.xinyirun.scm.core.system.mapper.master.user.MStaffMapper;
import com.xinyirun.scm.core.system.service.log.datachange.IDataChangeStrategyService;
import com.xinyirun.scm.core.system.serviceimpl.log.datachange.LogChangeBaseServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.binding.MapperMethod;
import org.apache.ibatis.mapping.BoundSql;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * 采购订单数据变更日志
 */
@Slf4j
@Service
public class DataChangeStrategyBPoOrderEntityServiceImpl extends LogChangeBaseServiceImpl implements IDataChangeStrategyService {

    @Autowired
    MStaffMapper staffMapper;

    @Autowired
    BPoOrderMapper poOrderMapper;

    /**
     * 获取数据新增的对象，还没有进行比对
     */
    @Override
    public SDataChangeLogDetailVo getDataChangeVoByInsert(BoundSql boundSql) {
        Object parameterObject = boundSql.getParameterObject();
        BPoOrderEntity entity = (BPoOrderEntity)parameterObject;
        SDataChangeLogDetailVo vo = new SDataChangeLogDetailVo();
        vo.setOrder_code(entity.getCode());
        vo.setClass_name(this.getClass().getCanonicalName());
        vo.setResult(JSON.toJSONString(entity, JSONWriter.Feature.LargeObject));
        vo.setResult_bean_name(entity.getClass().getCanonicalName());
        vo.setTableColumns(AnnotationUtil.getFieldNameAndDataChageLabel(entity));
        vo.setU_id(entity.getU_id());
        vo.setU_time(entity.getU_time());
        vo.setTable_id(entity.getId());
        vo.setEntity_name(BPoOrderEntity.class.getName());
        // 根据 u_id 获取 u_name
        MStaffVo staffVo = staffMapper.getDetail(entity.getU_id());
        if (staffVo!= null) {
            vo.setU_name(staffVo.getName());
        }

        return vo;
    }

    /**
     * 获取数据变更的对象，还没有进行比对，更新前
     */
    @Override
    public SDataChangeLogDetailVo getDataChangeVoByUpdateBefore(BoundSql boundSql) {
        Object parameterObject = boundSql.getParameterObject();
        // 查询更新前的数据
        BPoOrderEntity condition = (BPoOrderEntity)((MapperMethod.ParamMap) parameterObject).get("param1");
        BPoOrderEntity entity = poOrderMapper.selectById(condition.getId());

        SDataChangeLogDetailVo vo = new SDataChangeLogDetailVo();
        vo.setOrder_code(entity.getCode());
        vo.setClass_name(this.getClass().getCanonicalName());
        vo.setResult(JSON.toJSONString(entity, JSONWriter.Feature.LargeObject));
        vo.setResult_bean_name(entity.getClass().getCanonicalName());
        vo.setTableColumns(AnnotationUtil.getFieldNameAndDataChageLabel(entity));
        vo.setU_id(entity.getU_id());
        vo.setU_time(entity.getU_time());
        vo.setTable_id(entity.getId());
        vo.setEntity_name(BPoOrderEntity.class.getName());
        // 根据 u_id 获取 u_name
        MStaffVo staffVo = staffMapper.getDetail(entity.getU_id());
        if (staffVo!= null) {
            vo.setU_name(staffVo.getName());
        }

        return vo;
    }

    /**
     * 获取数据变更的对象，还没有进行比对，更新后
     */
    @Override
    public SDataChangeLogDetailVo getDataChangeVoByUpdateAfter(BoundSql boundSql) {
        Object parameterObject = boundSql.getParameterObject();
        BPoOrderEntity entity = (BPoOrderEntity)((MapperMethod.ParamMap) parameterObject).get("param1");
        SDataChangeLogDetailVo vo = new SDataChangeLogDetailVo();
        vo.setOrder_code(entity.getCode());
        vo.setClass_name(this.getClass().getCanonicalName());
        vo.setResult(JSON.toJSONString(entity, JSONWriter.Feature.LargeObject));
        vo.setResult_bean_name(entity.getClass().getCanonicalName());
        vo.setTableColumns(AnnotationUtil.getFieldNameAndDataChageLabel(entity));
        vo.setU_id(entity.getU_id());
        vo.setU_time(entity.getU_time());
        vo.setTable_id(entity.getId());
        vo.setEntity_name(BPoOrderEntity.class.getName());
        // 根据 u_id 获取 u_name
        MStaffVo staffVo = staffMapper.getDetail(entity.getU_id());
        if (staffVo!= null) {
            vo.setU_name(staffVo.getName());
        }

        return vo;
    }

    /**
     * 获取数据删除的对象，还没有进行比对
     * @param boundSql
     * @return
     */
    @Override
    public SDataChangeLogDetailVo getDataChangeVoByDelete(BoundSql boundSql) {
        Integer id = (Integer) boundSql.getParameterObject();
        BPoOrderEntity entity = poOrderMapper.selectById(id);
        SDataChangeLogDetailVo vo = new SDataChangeLogDetailVo();
        vo.setOrder_code(entity.getCode());
        vo.setClass_name(this.getClass().getCanonicalName());
        vo.setResult(JSON.toJSONString(entity, JSONWriter.Feature.LargeObject));
        vo.setResult_bean_name(entity.getClass().getCanonicalName());
        vo.setTableColumns(AnnotationUtil.getFieldNameAndDataChageLabel(entity));
        vo.setU_id(entity.getU_id());
        vo.setU_time(entity.getU_time());
        vo.setTable_id(entity.getId());
        vo.setEntity_name(BPoOrderEntity.class.getName());
        // 根据 u_id 获取 u_name
        MStaffVo staffVo = staffMapper.getDetail(entity.getU_id());
        if (staffVo!= null) {
            vo.setU_name(staffVo.getName());
        }

        return vo;
    }

    /**
     * 根据id获取单号order_code
     * @param id
     * @return
     */
    @Override
    public String getOrderCode(Integer id) {
        log.debug("------开始 获取 poOrderMapper code -----------");
        String code = poOrderMapper.selectById(id).getCode();
        log.debug("根据id取code：{}", code);
        log.debug("------结束 获取 poOrderMapper code -----------");
        return code;
    }

    /**
     * 根据id获取到c_time
     * DataChangeLabelAnnotation( extension = "getCTimeExtension")的扩展
     * @param param
     * @return
     */
    @Override
    public SLogDataChangeDetailVo getCTimeExtension(String param, String _json_data, String clm_name, String clm_label) {
        SLogDataChangeDetailVo vo = new SLogDataChangeDetailVo();
        vo.setClm_name(clm_name);
        vo.setClm_label(clm_label);

        if (!Objects.isNull(_json_data)) {
            // 获取字段信息
            LocalDateTime localDateTime = null;
            try {
                BPoOrderEntity entity = JSONObject.parseObject(_json_data, BPoOrderEntity.class);
                localDateTime = poOrderMapper.selectById(entity.getId()).getC_time();
            } catch (Exception e) {
                log.error("根据id获取到c_time", e);
            }
            vo.setNew_value(localDateTime);
            vo.setOld_value(localDateTime);
            log.debug("日志变更（变更前后）逻辑--根据id获取到c_time，参数：{},查找名称：{}", param, localDateTime);
        } else {
            log.debug("日志变更（变更前后）逻辑--根据id获取到c_time方法所传参数为空");
        }
        return vo;
    }

    /**
     * 根据id获取到u_time
     * DataChangeLabelAnnotation( extension = "getUTimeExtension")的扩展
     * @param param
     * @return
     */
    @Override
    public SLogDataChangeDetailVo getUTimeExtension(String param, String _json_data, String clm_name, String clm_label) {
        SLogDataChangeDetailVo vo = new SLogDataChangeDetailVo();
        vo.setClm_name(clm_name);
        vo.setClm_label(clm_label);

        if (!Objects.isNull(_json_data)) {
            // 获取字段信息
            LocalDateTime localDateTime = null;
            try {
                BPoOrderEntity entity = JSONObject.parseObject(_json_data, BPoOrderEntity.class);
                localDateTime = poOrderMapper.selectById(entity.getId()).getU_time();
            } catch (Exception e) {
                log.error("根据id获取到u_time", e);
            }

            vo.setNew_value(localDateTime);
            vo.setOld_value(localDateTime);
            log.debug("日志变更（变更前后）逻辑--根据id获取到u_time，参数：{},查找名称：{}", param, localDateTime);
        } else {
            log.debug("日志变更（变更前后）逻辑--根据id获取到u_time方法所传参数为空");
        }
        return vo;
    }
} 