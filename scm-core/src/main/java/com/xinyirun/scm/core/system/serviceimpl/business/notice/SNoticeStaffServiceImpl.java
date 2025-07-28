package com.xinyirun.scm.core.system.serviceimpl.business.notice;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xinyirun.scm.bean.entity.business.notice.BNoticeStaffEntity;
import com.xinyirun.scm.bean.system.vo.master.user.MStaffVo;
import com.xinyirun.scm.core.system.mapper.business.notice.BNoticeStaffMapper;
import com.xinyirun.scm.core.system.service.business.notice.IBNoticeStaffService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author xinyirun
 * @since 2024-01-29
 */
@Service
public class SNoticeStaffServiceImpl extends ServiceImpl<BNoticeStaffMapper, BNoticeStaffEntity> implements IBNoticeStaffService {

    /**
     * 新增关联关系
     *
     * @param notice_id b_notice表 id
     * @param staffList 员工列表
     */
    @Override
    public void insertNoticeStaff(Integer notice_id, List<MStaffVo> staffList) {
        Set<BNoticeStaffEntity> collect = staffList.stream().map(staff -> {
            BNoticeStaffEntity entity = new BNoticeStaffEntity();
            entity.setNotice_id(notice_id);
            entity.setStaff_id(Math.toIntExact(staff.getId()));
            return entity;
        }).collect(Collectors.toSet());
        this.saveBatch(collect);
    }

    /**
     * 更新关联关系
     *
     * @param notice_id        b_notice表 id
     * @param staffList 员工列表
     */
    @Override
    public void updateNoticeStaff(Integer notice_id, List<MStaffVo> staffList) {
        // 删除原有关联关系
        this.removeByMap(Map.of("notice_id", notice_id));

        // 新增关联关系
        this.insertNoticeStaff(notice_id, staffList);

    }

    /**
     * 查询通知员工列表
     *
     * @param notice_id b_notice表 id
     */
    @Override
    public List<MStaffVo> selectStaffList(Integer notice_id) {
        return baseMapper.selectStaffListByNoticeId(notice_id);
    }

    /**
     * 更新状态 为 已读
     *
     * @param id
     * @param staffId
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateIsRead(Integer id, Long staffId) {
        baseMapper.update(Wrappers.<BNoticeStaffEntity>lambdaUpdate().eq(BNoticeStaffEntity::getNotice_id, id)
                .eq(BNoticeStaffEntity::getStaff_id, staffId).set(BNoticeStaffEntity::getIs_read, 1));
    }
}
