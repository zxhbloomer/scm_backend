package com.xinyirun.scm.core.system.serviceimpl.log.datachange;

import com.xinyirun.scm.bean.entity.master.enterprise.MEnterpriseEntity;
import com.xinyirun.scm.bean.entity.master.goods.unit.MUnitEntity;
import com.xinyirun.scm.bean.system.vo.master.driver.MDriverVo;
import com.xinyirun.scm.bean.system.vo.master.user.MStaffVo;
import com.xinyirun.scm.bean.system.vo.master.vehicle.MVehicleVo;
import com.xinyirun.scm.bean.system.vo.clickhouse.log.SLogDataChangeDetailVo;
import com.xinyirun.scm.bean.system.vo.sys.config.dict.SDictDataVo;
import com.xinyirun.scm.bean.system.vo.sys.file.SFileInfoVo;
import com.xinyirun.scm.core.system.mapper.master.customer.MCustomerMapper;
import com.xinyirun.scm.core.system.mapper.master.driver.MDriverMapper;
import com.xinyirun.scm.core.system.mapper.master.enterpise.MEnterpriseMapper;
import com.xinyirun.scm.core.system.mapper.master.goods.unit.MUnitMapper;
import com.xinyirun.scm.core.system.mapper.master.user.MStaffMapper;
import com.xinyirun.scm.core.system.mapper.master.vehicle.MVehicleMapper;
import com.xinyirun.scm.core.system.mapper.master.warehouse.MWarehouseMapper;
import com.xinyirun.scm.core.system.mapper.sys.config.dict.SDictDataMapper;
import com.xinyirun.scm.core.system.mapper.sys.file.SFileInfoMapper;
import com.xinyirun.scm.core.system.service.log.datachange.LogChangeBaseService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

/**
 * 数据变更日志公共实现
 * 注意：
 * 1、不能使用@DataChangeEntityAnnotation注解，因为这个是公共的，不是具体的实体类直接调用，需要通过反射调用
 * 2、因为通过了mybatisplus的拦截器，所以事务在这里除了问题，所以查询数据需要有时间的代价，也就是定时任务来发起
 */
@Slf4j
@Service
public class LogChangeBaseServiceImpl implements LogChangeBaseService {

    @Autowired
    MWarehouseMapper mWarehouseMapper;

    @Autowired
    MStaffMapper staffMapper;

    @Autowired
    MDriverMapper driverMapper;

    @Autowired
    MVehicleMapper vehicleMapper;

    @Autowired
    MCustomerMapper customerMapper;

    @Autowired
    SFileInfoMapper sFileInfoMapper;

    @Autowired
    SDictDataMapper sDictMapper;

    @Autowired
    MEnterpriseMapper mEnterpriseMapper;

    @Autowired
    MUnitMapper mUnitMapper;

    /**
     * 根据id获取仓库名称
     * DataChangeLabelAnnotation(value = "仓库id", extension = "getWarehouseNameExtension")的扩展
     * @param param
     * @return
     */
    public SLogDataChangeDetailVo getWarehouseNameExtension(String param, String _data, String clm_name, String clm_label) {
        SLogDataChangeDetailVo vo = new SLogDataChangeDetailVo();
        vo.setNew_value("");
        vo.setOld_value("");
        vo.setClm_name(clm_name);
        vo.setClm_label(clm_label);

        if (!Objects.isNull(param)) {
            // 获取字段信息
            String name = null;
            try {
                name = mWarehouseMapper.selectId(Integer.parseInt(param)).getName();
            } catch (Exception e) {
                log.error("根据id获取仓库名称", e);
            }
            vo.setNew_value(name);
            vo.setOld_value(name);
            log.debug("日志变更（变更前后）逻辑--根据id获取仓库名称方法，参数：{},查找名称：{}", param, name);
        } else {
            log.debug("日志变更（变更前后）逻辑--根据id获取仓库名称方法所传参数为空");
        }
        return vo;
    }

    /**
     * 根据id获取用户名称，staff
     * DataChangeLabelAnnotation( extension = "getUserNameExtension")的扩展
     * @param param
     * @return
     */
    public SLogDataChangeDetailVo getUserNameExtension(String param, String _data, String clm_name, String clm_label) {
        log.debug("---------------getUserNameExtension-----------------");
        SLogDataChangeDetailVo vo = new SLogDataChangeDetailVo();
        vo.setNew_value("");
        vo.setOld_value("");
        vo.setClm_name(clm_name);
        vo.setClm_label(clm_label);

        if (!Objects.isNull(param)) {
            MStaffVo staffVo = null;
            try {
                staffVo = staffMapper.getDetail(Long.valueOf(param));
            } catch (Exception e) {
                log.error("根据id获取用户名称，staff", e);
            }
            if (staffVo!= null) {
                vo.setNew_value(staffVo.getUser());
                vo.setOld_value(staffVo.getUser());
                log.debug("日志变更（变更前后）逻辑--根据id获取用户名称方法，参数：{},查找名称：{}", param, staffVo.getUser());
            } else {
                log.debug("日志变更（变更前后）逻辑--根据id获取司机名称方法，参数：{}，参数：{},但是查找出的数据为Null", param );
            }
        } else {
            log.debug("日志变更（变更前后）逻辑--根据id获取用户名称方法所传参数为空");
        }
        return vo;
    }

    /**
     * 根据id获取司机名称
     * DataChangeLabelAnnotation( extension = "getDriverNameExtension")的扩展
     * @param param
     * @return
     */
    public SLogDataChangeDetailVo getDriverNameExtension(String param, String _data, String clm_name, String clm_label) {
        SLogDataChangeDetailVo vo = new SLogDataChangeDetailVo();
        vo.setNew_value("");
        vo.setOld_value("");
        vo.setClm_name(clm_name);
        vo.setClm_label(clm_label);

        if (!Objects.isNull(param)) {
            MDriverVo searchCondition = new MDriverVo();
            searchCondition.setId(Integer.valueOf(param));
            MDriverVo mDriverVo = null;
            try {
                mDriverVo = driverMapper.getDetail(searchCondition);
            } catch (Exception e) {
                log.error("根据id获取司机名称", e);
            }
            if (mDriverVo!= null) {
                vo.setNew_value(mDriverVo.getName());
                vo.setOld_value(mDriverVo.getName());
                log.debug("日志变更（变更前后）逻辑--根据id获取司机名称方法，参数：{},查找名称：{}", param, mDriverVo.getName());
            } else {
                log.debug("日志变更（变更前后）逻辑--根据id获取司机名称方法，参数：{}，参数：{},但是查找出的数据为Null", param );
            }
        } else {
            log.debug("日志变更（变更前后）逻辑--根据id获取司机名称方法所传参数为空");
        }
        return vo;
    }

    /**
     * 根据id获取车牌号
     * DataChangeLabelAnnotation( extension = "getVehicleNameExtension")的扩展
     * @param param
     * @return
     */
    public SLogDataChangeDetailVo getVehicleNoExtension(String param, String _data, String clm_name, String clm_label) {
        SLogDataChangeDetailVo vo = new SLogDataChangeDetailVo();
        vo.setNew_value("");
        vo.setOld_value("");
        vo.setClm_name(clm_name);
        vo.setClm_label(clm_label);

        if (!Objects.isNull(param)) {
            MVehicleVo searchCondition = new MVehicleVo();
            searchCondition.setId(Integer.valueOf(param));
            MVehicleVo vehicleVo = null;
            try {
                vehicleVo = vehicleMapper.getDetail(searchCondition);
            } catch (Exception e) {
                log.error("根据id获取车牌号", e);
            }
            if (vehicleVo!= null) {
                vo.setNew_value(vehicleVo.getNo());
                vo.setOld_value(vehicleVo.getNo());
                log.debug("日志变更（变更前后）逻辑--根据id获取车牌号方法，参数：{},查找名称：{}", param, vehicleVo.getNo());
            } else {
                log.debug("日志变更（变更前后）逻辑--根据id获取车牌号方法，参数：{},但是查找出的数据为Null", param );
            }
        } else {
            log.debug("日志变更（变更前后）逻辑--根据id获取车牌号方法所传参数为空");
        }
        return vo;
    }

//    /**
//     * 根据id获取货主名称
//     * DataChangeLabelAnnotation( extension = "getCustomerNameExtension")的扩展
//     * @param param
//     * @return
//     */
//    public SLogDataChangeDetailVo getCustomerNameExtension(String param, String _data, String clm_name, String clm_label) {
//        SLogDataChangeDetailVo vo = new SLogDataChangeDetailVo();
//        vo.setNew_value("");
//        vo.setOld_value("");
//        vo.setClm_name(clm_name);
//        vo.setClm_label(clm_label);
//
//        if (!Objects.isNull(param)) {
//            MCustomerEntity customerEntity = null;
//            try {
//                customerEntity = customerMapper.selectById(param);
//            } catch (Exception e) {
//                log.error("根据id获取货主名称", e);
//            }
//            if (customerEntity!= null) {
//                vo.setNew_value(customerEntity.getName());
//                vo.setOld_value(customerEntity.getName());
//                log.debug("日志变更（变更前后）逻辑--根据id获取货主名称方法，参数：{},查找名称：{}", param, customerEntity.getName());
//            } else {
//                log.debug("日志变更（变更前后）逻辑--根据id获取货主名称方法，参数：{},但是查找出的数据为Null", param);
//            }
//        } else {
//            log.debug("日志变更（变更前后）逻辑--根据id获取货主名称方法所传参数为空");
//        }
//        return vo;
//    }

    /**
     * 根据附件id获取附件URL信息
     * DataChangeLabelAnnotation( extension = "getAttachmentUrlExtension")的扩展
     * @param param
     * @return
     */
    public SLogDataChangeDetailVo getAttachmentUrlExtension(String param, String _data, String clm_name, String clm_label) {
        SLogDataChangeDetailVo vo = new SLogDataChangeDetailVo();
        vo.setNew_value("");
        vo.setOld_value("");
        vo.setClm_name(clm_name);
        vo.setClm_label(clm_label);

        if (!Objects.isNull(param)) {
            List<SFileInfoVo> sFileInfoVoList = null;
            try {
                sFileInfoVoList = sFileInfoMapper.selectFIdList(Integer.parseInt(param));
            } catch (Exception e) {
                log.error("根据附件id获取附件URL信息，s_file_info", e);
            }
            if (sFileInfoVoList != null && !sFileInfoVoList.isEmpty()) {
                // 循环拼接字符串
                StringBuilder attachmentInfo = new StringBuilder();
                attachmentInfo.append("共").append(sFileInfoVoList.size()).append("个附件：");

                for (int i = 0; i < sFileInfoVoList.size(); i++) {
                    if (i > 0) {
                        attachmentInfo.append(";");
                    }
                    attachmentInfo.append("附件").append(i + 1).append("-").append(sFileInfoVoList.get(i).getUrl());
                }

                String result = attachmentInfo.toString();
                vo.setNew_value(result);
                vo.setOld_value(result);
                log.debug("日志变更（变更前后）逻辑--根据id获取附件URL信息方法，参数：{},查找结果：{}", param, result);
            } else {
                log.debug("日志变更（变更前后）逻辑--根据id获取附件URL信息方法，参数：{},但是查找出的数据为Null或空列表", param);
            }
        } else {
            log.debug("日志变更（变更前后）逻辑--根据id获取附件URL信息方法所传参数为空");
        }
        return vo;
    }

    /**
     * 根据字典value获取字典label
     * @DataChangeLabelAnnotation(value="xxxxx", dictExtension = "getDictExtension", dictExtensionType = "com.xinyirun.scm.common.constant.DictConstant.xxxxxx")的扩展
     * @param param
     * @return
     */
    public SLogDataChangeDetailVo getDictExtension(String dictType, String param, String _data, String clm_name, String clm_label) {
        SLogDataChangeDetailVo vo = new SLogDataChangeDetailVo();
        vo.setNew_value("");
        vo.setOld_value("");
        vo.setClm_name(clm_name);
        vo.setClm_label(clm_label);

        if (!Objects.isNull(param)) {
            SDictDataVo sDictDataVo = null;
            try {
                // 通过反射获取常量值
                String dictTypeValue = getDictTypeValue(dictType);
                sDictDataVo = sDictMapper.getDetailByCodeAndDictValue(dictTypeValue, param);

                if (sDictDataVo!= null) {
                    vo.setNew_value(sDictDataVo.getLabel());
                    vo.setOld_value(sDictDataVo.getLabel());
                    log.debug("日志变更（变更前后）逻辑--根据value获取字典label，参数：{},查找名称：{}", param, sDictDataVo.getLabel());
                } else {
                    log.debug("日志变更（变更前后）逻辑--根据value获取字典label，参数：{},但是查找出的数据为Null", param);
                }
            } catch (Exception e) {
                log.error("根据字典value获取字典名称", e);
            }
        } else {
            log.debug("日志变更（变更前后）逻辑--根据value获取字典label方法所传参数为空");
        }
        return vo;
    }

    /**
     * 通过反射获取常量值
     * @param dictTypeStr 字典类型的完全限定名称
     * @return 常量的实际值
     */
    private String getDictTypeValue(String dictTypeStr) {
        try {
            // 分割字符串获取类名和字段名
            int lastDotIndex = dictTypeStr.lastIndexOf('.');
            String className = dictTypeStr.substring(0, lastDotIndex);
            String fieldName = dictTypeStr.substring(lastDotIndex + 1);

            // 加载类
            Class<?> clazz = Class.forName(className);

            // 获取静态字段
            java.lang.reflect.Field field = clazz.getDeclaredField(fieldName);

            // 获取字段的值（静态字段不需要实例）
            return (String) field.get(null);
        } catch (Exception e) {
            log.error("通过反射获取字典类型值失败: " + dictTypeStr, e);
            return dictTypeStr; // 失败时返回原始字符串
        }
    }

    /**
     * 根据id获取供应商名称
     * DataChangeLabelAnnotation( extension = "getSupplierNameExtension")的扩展
     * @param param
     * @return
     */
    public SLogDataChangeDetailVo getSupplierNameExtension(String param, String _data, String clm_name, String clm_label) {
        SLogDataChangeDetailVo vo = new SLogDataChangeDetailVo();
        vo.setNew_value("");
        vo.setOld_value("");
        vo.setClm_name(clm_name);
        vo.setClm_label(clm_label);

        if (!Objects.isNull(param)) {
            MEnterpriseEntity entity = null;
            try {
                entity = mEnterpriseMapper.selectById(param);
            } catch (Exception e) {
                log.error("根据id获取货主名称", e);
            }
            if (entity!= null) {
                vo.setNew_value(entity.getName());
                vo.setOld_value(entity.getName());
                log.debug("日志变更（变更前后）逻辑--根据id获取供应商名称方法，参数：{},查找名称：{}", param, entity.getName());
            } else {
                log.debug("日志变更（变更前后）逻辑--根据id获取供应商名称方法，参数：{},但是查找出的数据为Null", param);
            }
        } else {
            log.debug("日志变更（变更前后）逻辑--根据id获取供应商名称方法所传参数为空");
        }
        return vo;
    }

    /**
     * 根据id获取客户名称
     * DataChangeLabelAnnotation( extension = "getCustomerNameExtension")的扩展
     * @param param
     * @return
     */
    public SLogDataChangeDetailVo getCustomerNameExtension(String param, String _data, String clm_name, String clm_label) {
        SLogDataChangeDetailVo vo = new SLogDataChangeDetailVo();
        vo.setNew_value("");
        vo.setOld_value("");
        vo.setClm_name(clm_name);
        vo.setClm_label(clm_label);

        if (!Objects.isNull(param)) {
            MEnterpriseEntity entity = null;
            try {
                entity = mEnterpriseMapper.selectById(param);
            } catch (Exception e) {
                log.error("根据id获取货主名称", e);
            }
            if (entity!= null) {
                vo.setNew_value(entity.getName());
                vo.setOld_value(entity.getName());
                log.debug("日志变更（变更前后）逻辑--根据id获取客户名称方法，参数：{},查找名称：{}", param, entity.getName());
            } else {
                log.debug("日志变更（变更前后）逻辑--根据id获取客户名称方法，参数：{},但是查找出的数据为Null", param);
            }
        } else {
            log.debug("日志变更（变更前后）逻辑--根据id获取客户名称方法所传参数为空");
        }
        return vo;
    }

    /**
     * 根据id获取单位名称
     * DataChangeLabelAnnotation( extension = "getCustomerNameExtension")的扩展
     * @param param
     * @return
     */
    public SLogDataChangeDetailVo getUnitNameExtension(String param, String _data, String clm_name, String clm_label) {
        SLogDataChangeDetailVo vo = new SLogDataChangeDetailVo();
        vo.setNew_value("");
        vo.setOld_value("");
        vo.setClm_name(clm_name);
        vo.setClm_label(clm_label);

        if (!Objects.isNull(param)) {
            MUnitEntity entity = null;
            try {
                entity = mUnitMapper.selectById(param);
            } catch (Exception e) {
                log.error("根据id获取货主名称", e);
            }
            if (entity!= null) {
                vo.setNew_value(entity.getName());
                vo.setOld_value(entity.getName());
                log.debug("日志变更（变更前后）逻辑--根据id获取单位名称方法，参数：{},查找名称：{}", param, entity.getName());
            } else {
                log.debug("日志变更（变更前后）逻辑--根据id获取单位名称方法，参数：{},但是查找出的数据为Null", param);
            }
        } else {
            log.debug("日志变更（变更前后）逻辑--根据id获取单位名称方法所传参数为空");
        }
        return vo;
    }

    /**
     * 根据id获取企业名称（用于监管公司和运营公司）
     * DataChangeLabelAnnotation(value = "企业", extension = "getEnterpriseNameExtension")的扩展
     * @param param 企业ID
     * @return 企业名称信息
     */
    public SLogDataChangeDetailVo getEnterpriseNameExtension(String param, String _data, String clm_name, String clm_label) {
        SLogDataChangeDetailVo vo = new SLogDataChangeDetailVo();
        vo.setNew_value("");
        vo.setOld_value("");
        vo.setClm_name(clm_name);
        vo.setClm_label(clm_label);

        if (!Objects.isNull(param)) {
            MEnterpriseEntity entity = null;
            try {
                entity = mEnterpriseMapper.selectById(Integer.parseInt(param));
            } catch (Exception e) {
                log.error("根据id获取企业名称", e);
            }
            if (entity != null) {
                vo.setNew_value(entity.getName());
                vo.setOld_value(entity.getName());
                log.debug("日志变更（变更前后）逻辑--根据id获取企业名称方法，参数：{},查找名称：{}", param, entity.getName());
            } else {
                log.debug("日志变更（变更前后）逻辑--根据id获取企业名称方法，参数：{},但是查找出的数据为Null", param);
            }
        } else {
            log.debug("日志变更（变更前后）逻辑--根据id获取企业名称方法所传参数为空");
        }
        return vo;
    }
}
