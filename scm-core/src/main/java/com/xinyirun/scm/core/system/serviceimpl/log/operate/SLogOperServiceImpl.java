package com.xinyirun.scm.core.system.serviceimpl.log.operate;

import com.xinyirun.scm.bean.system.ao.result.InsertResultAo;
import com.xinyirun.scm.bean.system.bo.log.operate.CustomOperateBo;
import com.xinyirun.scm.bean.system.bo.log.operate.CustomOperateDetailBo;
import com.xinyirun.scm.bean.system.bo.session.user.system.UserSessionBo;
import com.xinyirun.scm.bean.entity.log.operate.SLogOperDetailEntity;
import com.xinyirun.scm.bean.entity.log.operate.SLogOperEntity;
import com.xinyirun.scm.bean.system.result.utils.v1.InsertResultUtil;
import com.xinyirun.scm.bean.system.utils.servlet.ServletUtil;
import com.xinyirun.scm.common.constant.SystemConstants;
import com.xinyirun.scm.common.exception.system.BusinessException;
import com.xinyirun.scm.common.utils.bean.BeanUtilsSupport;
import com.xinyirun.scm.core.system.mapper.log.operate.SLogOperMapper;
import com.xinyirun.scm.core.system.service.log.operate.ISLogOperService;
import com.xinyirun.scm.core.system.serviceimpl.base.v1.BaseServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.*;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author zxh
 * @since 2019-07-04
 */
@Service
public class SLogOperServiceImpl extends BaseServiceImpl<SLogOperMapper, SLogOperEntity> implements ISLogOperService {

    @Autowired
    SLogOperMapper mapper;

    @Autowired
    SLogOperDetailServiceImpl sLogOperDetailService;

    /**
     * 插入记录，包含主表和从表
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public InsertResultAo<Boolean> save(CustomOperateBo cobo) {
        SLogOperEntity sLogOperEntity = (SLogOperEntity)BeanUtilsSupport.copyProperties(cobo, SLogOperEntity.class);
        sLogOperEntity.setOper_name(ServletUtil.getUserSession().getStaff_info().getName());
        sLogOperEntity.setOper_time(LocalDateTime.now());
        sLogOperEntity.setOper_id(((UserSessionBo)ServletUtil.getUserSession()).getAccountId());
        sLogOperEntity.setType(cobo.getType().getName());
        mapper.insert(sLogOperEntity);

        // 定义子表的bean
        List<SLogOperDetailEntity> sLogOperDetailEntities = new ArrayList<>();
        List<CustomOperateDetailBo> detail = cobo.getDetail();
        for(CustomOperateDetailBo bo : detail){

            Iterator<Map.Entry<String, String>> entries = bo.getColumns().entrySet().iterator();
            while (entries.hasNext()) {
                Map.Entry<String, String> entry = entries.next();

                SLogOperDetailEntity sLogOperDetailEntity = new SLogOperDetailEntity();
                sLogOperDetailEntity.setOper_id(sLogOperEntity.getId());
                sLogOperDetailEntity.setName(bo.getName());
                sLogOperDetailEntity.setType(bo.getType().getName());
                sLogOperDetailEntity.setOper_info(bo.getOper_info());
                sLogOperDetailEntity.setTable_name(bo.getTable_name());
                sLogOperDetailEntity.setClm_name(entry.getKey());
                sLogOperDetailEntity.setClm_comment(entry.getValue());

                // set old value
                if (Objects.isNull(bo.getOldData())) {
                    sLogOperDetailEntity.setOld_val(null);
                } else {
                    try {
                        Field field = bo.getOldData().getClass().getDeclaredField(entry.getKey());
                        field.setAccessible(true);
                        Object value = field.get(bo.getOldData());
                        sLogOperDetailEntity.setOld_val(value.toString());
                    } catch (NoSuchFieldException e) {
                        throw new BusinessException("执行操作日志记录出错，未找到方法：" + entry.getKey() + "(" + entry.getValue() + ")");
                    } catch (IllegalAccessException e) {
                        throw new BusinessException("执行操作日志记录出错，未找到方法：" + entry.getKey() + "(" + entry.getValue() + ")");
                    }
                }

                // set new value
                if (Objects.isNull(bo.getNewData())) {
                    sLogOperDetailEntity.setNew_val(null);
                } else {
                    try {
                        Field field = bo.getNewData().getClass().getDeclaredField(entry.getKey());
                        field.setAccessible(true);
                        Object value = field.get(bo.getNewData());
                        sLogOperDetailEntity.setNew_val(value.toString());
                    } catch (NoSuchFieldException e) {
                        throw new BusinessException("执行操作日志记录出错，未找到方法：" + entry.getKey() + "(" + entry.getValue() + ")");
                    } catch (IllegalAccessException e) {
                        throw new BusinessException("执行操作日志记录出错，未找到方法：" + entry.getKey() + "(" + entry.getValue() + ")");
                    }
                }

                sLogOperDetailEntity.setTable_name(SystemConstants.OPERATION.M_STAFF_ORG.TABLE_NAME);
                sLogOperDetailEntities.add(sLogOperDetailEntity);
            }
        }
        sLogOperDetailService.saveBatch(sLogOperDetailEntities);

        return InsertResultUtil.OK(true);
    }
}
