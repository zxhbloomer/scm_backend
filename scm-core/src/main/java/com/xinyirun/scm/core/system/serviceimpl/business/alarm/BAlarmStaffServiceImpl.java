package com.xinyirun.scm.core.system.serviceimpl.business.alarm;

import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xinyirun.scm.bean.entity.busniess.alarm.BAlarmGroupStaffEntity;
import com.xinyirun.scm.bean.entity.busniess.alarm.BAlarmStaffEntity;
import com.xinyirun.scm.bean.system.ao.result.InsertResultAo;
import com.xinyirun.scm.bean.system.ao.result.UpdateResultAo;
import com.xinyirun.scm.bean.system.result.utils.v1.InsertResultUtil;
import com.xinyirun.scm.bean.system.vo.business.alarm.BAlarmStaffVo;
import com.xinyirun.scm.common.exception.system.BusinessException;
import com.xinyirun.scm.common.exception.system.InsertErrorException;
import com.xinyirun.scm.common.utils.bean.BeanUtilsSupport;
import com.xinyirun.scm.core.system.mapper.business.alarm.BAlarmStaffMapper;
import com.xinyirun.scm.core.system.service.business.alarm.IBAlarmGroupStaffService;
import com.xinyirun.scm.core.system.service.business.alarm.IBAlarmStaffService;
import com.xinyirun.scm.core.system.serviceimpl.common.autocode.BAlarmStaffCodeServiceImpl;
import com.xinyirun.scm.core.system.utils.mybatis.PageUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author xinyirun
 * @since 2022-12-16
 */
@Service
public class BAlarmStaffServiceImpl extends ServiceImpl<BAlarmStaffMapper, BAlarmStaffEntity> implements IBAlarmStaffService {

    @Autowired
    private BAlarmStaffCodeServiceImpl autoCode;

    @Autowired
    private IBAlarmGroupStaffService groupStaffService;

    @Autowired
    private BAlarmStaffMapper mapper;

    /**
     * 新增 预警人员
     *
     * @param vo 入参
     * @return InsertResultAo<BAlarmGroupVo>
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public InsertResultAo<BAlarmStaffVo> insert(BAlarmStaffVo vo) {
        // 判断是否重复添加
        checkRepeatInsert(vo);
        BAlarmStaffEntity entity = (BAlarmStaffEntity) BeanUtilsSupport.copyProperties(vo, BAlarmStaffEntity.class);
        entity.setCode(autoCode.autoCode().getCode());
        int rtn = mapper.insert(entity);
        if (null != vo.getGroup_id()) {
            // 添加关联表
            BAlarmGroupStaffEntity bAlarmGroupStaffEntity = new BAlarmGroupStaffEntity(entity.getId(), vo.getGroup_id());
            groupStaffService.save(bAlarmGroupStaffEntity);
        }
        if (rtn == 0) {
            throw new InsertErrorException("新增保存失败。");
        }
        BAlarmStaffVo result = mapper.selectVoById(entity.getId());
        result.setGroup_name_list(result.getGroup_name_list().stream().filter(item1 -> null != item1.get("name")).collect(Collectors.toList()));
        return InsertResultUtil.OK(result);
    }

    /**
     * 预警人员 列表查询
     *
     * @param vo
     * @return
     */
    @Override
    public IPage<BAlarmStaffVo> selectPageList(BAlarmStaffVo vo) {
        Page<BAlarmStaffVo> page = new Page<>(vo.getPageCondition().getCurrent(), vo.getPageCondition().getSize());
        PageUtil.setSort(page, vo.getPageCondition().getSort());
        IPage<BAlarmStaffVo> result = mapper.selectPageList(page, vo);
        result.getRecords().forEach(item -> {
            List<JSONObject> name = item.getGroup_name_list().stream().filter(item1 -> null != item1.get("name")).collect(Collectors.toList());
            item.setGroup_name_list(name);
        });
        return result;
    }

    /**
     * 预警人员 更新
     *
     * @param vo
     * @return
     */
    @Override
    public UpdateResultAo<BAlarmStaffVo> updateStaff(BAlarmStaffVo vo) {
        Assert.notNull(vo.getId(), "ID不能为空");
        checkRepeatInsert(vo);
        return null;
    }

    /**
     * 判断是否重复添加员工
     * @param vo
     */
    private void checkRepeatInsert(BAlarmStaffVo vo) {
        List<BAlarmStaffVo> bAlarmStaffVos = mapper.selectByStaff(vo.getStaff_id());
        if (!CollectionUtils.isEmpty(bAlarmStaffVos)) {
            throw new BusinessException("请勿重复添加员工");
        }
    }
}
