package com.xinyirun.scm.core.system.serviceimpl.business.alarm;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xinyirun.scm.bean.entity.business.alarm.BAlarmGroupStaffEntity;
import com.xinyirun.scm.core.system.mapper.business.alarm.BAlarmGroupStaffMapper;
import com.xinyirun.scm.core.system.service.business.alarm.IBAlarmGroupStaffService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
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
public class BAlarmGroupStaffServiceImpl extends ServiceImpl<BAlarmGroupStaffMapper, BAlarmGroupStaffEntity> implements IBAlarmGroupStaffService {

    @Autowired
    private BAlarmGroupStaffMapper mapper;

    /**
     * 根据预警组查询预警人员
     * @param groupList
     * @return
     */
    @Override
    public Set<Integer> selectStaffIdsByGroupIds(Set<Integer> groupList) {
        Set<Integer> result = new HashSet<>();
        List<BAlarmGroupStaffEntity> list = mapper.selectList(new LambdaQueryWrapper<BAlarmGroupStaffEntity>()
                .in(BAlarmGroupStaffEntity::getAlarm_group_id, groupList));
        if (!CollectionUtils.isEmpty(list)) {
            result = list.stream().map(BAlarmGroupStaffEntity::getStaff_id).collect(Collectors.toSet());
        }
        return result;
    }
}
