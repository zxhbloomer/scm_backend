package com.xinyirun.scm.core.system.serviceimpl.business.alarm;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xinyirun.scm.bean.entity.busniess.alarm.BAlarmRulesGroupEntity;
import com.xinyirun.scm.bean.system.vo.business.alarm.BAlarmStaffGroupTransferVo;
import com.xinyirun.scm.bean.system.vo.business.alarm.BAlarmStaffTransferVo;
import com.xinyirun.scm.common.constant.DictConstant;
import com.xinyirun.scm.core.system.mapper.business.alarm.BAlarmRulesGroupMapper;
import com.xinyirun.scm.core.system.service.business.alarm.IBAlarmGroupStaffService;
import com.xinyirun.scm.core.system.service.business.alarm.IBAlarmRulesGroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author xinyirun
 * @since 2023-03-15
 */
@Service
public class BAlarmRulesGroupServiceImpl extends ServiceImpl<BAlarmRulesGroupMapper, BAlarmRulesGroupEntity> implements IBAlarmRulesGroupService {

    @Autowired
    private BAlarmRulesGroupMapper mapper;

    @Autowired
    private IBAlarmGroupStaffService groupStaffService;

    /**
     * 根据 rules_id 查询人员
     * @param rules
     * @return
     */
/*    @Override
    public Set<Integer> selectStaffListBy(Set<Integer> rules) {
        List<BAlarmRulesGroupEntity> list = mapper.selectList(new LambdaQueryWrapper<BAlarmRulesGroupEntity>().in(BAlarmRulesGroupEntity::getAlarm_id, rules));
        Set<Integer> result = new HashSet<>();
        // 预警组
        Set<Integer> groupList = new HashSet<>();
        list.forEach(item -> {
            if (DictConstant.DICT_B_ALARM_RULES_STAFF_TYPE_2.equals(item.getType())) {
                groupList.add(item.getAlarm_group_id());
            } else {
                result.add(item.getStaff_id());
            }
        });
        // 判断是否有预警组, 如果有, 查询预警组内人员
        if (!CollectionUtils.isEmpty(groupList)) {
            Set<Integer> staffIds = groupStaffService.selectStaffIdsByGroupIds(groupList);
            result.addAll(staffIds);
        }
        return result;
    }*/

    /**
     * 预警人员设置
     *
     * @param bean
     * @return
     */
    @Override
    public void setStaffTransfer(BAlarmStaffTransferVo bean) {
        // 删除当前 规则下 的所有员工
        mapper.delete(new LambdaQueryWrapper<BAlarmRulesGroupEntity>()
                .eq(BAlarmRulesGroupEntity::getAlarm_id, bean.getAlarm_id())
                .eq(BAlarmRulesGroupEntity::getType, DictConstant.DICT_B_ALARM_RULES_STAFF_TYPE_1));
        // 新增
        List<BAlarmRulesGroupEntity> insertList = Arrays.stream(bean.getStaff_alarm()).map(item -> {
            BAlarmRulesGroupEntity entity = new BAlarmRulesGroupEntity();
            entity.setAlarm_id(bean.getAlarm_id());
            entity.setStaff_id(item);
            entity.setType(DictConstant.DICT_B_ALARM_RULES_STAFF_TYPE_1);
            return entity;
        }).collect(Collectors.toList());
        this.saveBatch(insertList);
    }

    /**
     * 查询预警组下所有员工
     *
     * @param bean
     * @return
     */
    @Override
    public BAlarmStaffGroupTransferVo getStaffTransferList(BAlarmStaffTransferVo bean) {
        BAlarmStaffGroupTransferVo rtn = new BAlarmStaffGroupTransferVo();
        rtn.setStaff_all(mapper.getAllStaffTransferList());
        // 查询已添加的员工
        List<Long> rtnList = baseMapper.getUsedStaffTransferList(bean.getAlarm_id());
        rtn.setStaff_alarm(rtnList.toArray(new Long[rtnList.size()]));
        return rtn;
    }

    /**
     * 查询预警组穿梭框
     *
     * @param bean
     * @return
     */
    @Override
    public BAlarmStaffGroupTransferVo getGroupTransferList(BAlarmStaffTransferVo bean) {
        BAlarmStaffGroupTransferVo rtn = new BAlarmStaffGroupTransferVo();
        rtn.setStaff_all(mapper.getAllGroupTransferList());
        // 查询已添加的员工
        List<Long> rtnList = baseMapper.getUsedGroupTransferList(bean.getAlarm_id());
        rtn.setStaff_alarm(rtnList.toArray(new Long[rtnList.size()]));
        return rtn;
    }

    /**
     * 新增预警组
     *
     * @param bean
     */
    @Override
    public void setGroupTransfer(BAlarmStaffTransferVo bean) {
        // 删除当前 规则下 的所有员工
        mapper.delete(new LambdaQueryWrapper<BAlarmRulesGroupEntity>()
                .eq(BAlarmRulesGroupEntity::getAlarm_id, bean.getAlarm_id())
                .eq(BAlarmRulesGroupEntity::getType, DictConstant.DICT_B_ALARM_RULES_STAFF_TYPE_2));
        // 新增
        List<BAlarmRulesGroupEntity> insertList = Arrays.stream(bean.getStaff_alarm()).map(item -> {
            BAlarmRulesGroupEntity entity = new BAlarmRulesGroupEntity();
            entity.setAlarm_id(bean.getAlarm_id());
            entity.setAlarm_group_id(item);
            entity.setType(DictConstant.DICT_B_ALARM_RULES_STAFF_TYPE_2);
            return entity;
        }).collect(Collectors.toList());
        this.saveBatch(insertList);
    }


}
