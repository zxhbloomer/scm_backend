package com.xinyirun.scm.core.bpm.service.business.notice;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xinyirun.scm.bean.entity.busniess.notice.BNoticeStaffEntity;
import com.xinyirun.scm.bean.system.vo.master.user.MStaffVo;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author xinyirun
 * @since 2024-01-29
 */
public interface IBpmBNoticeStaffService extends IService<BNoticeStaffEntity> {

    /**
     * 新增关联关系
     * @param notice_id b_notice表 id
     * @param staffList 员工列表
     */
    void insertNoticeStaff(Integer notice_id, List<MStaffVo> staffList);

    /**
     * 更新关联关系
     * @param notice_id b_notice表 id
     * @param staffList 员工列表
     */
    void updateNoticeStaff(Integer notice_id, List<MStaffVo> staffList);

    /**
     * 查询通知员工列表
     * @param id
     */
    List<MStaffVo> selectStaffList(Integer id);

    /**
     * 更新状态 为 已读
     * @param id
     * @param staffId
     */
    void updateIsRead(Integer id, Long staffId);
}
