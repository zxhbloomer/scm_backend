package com.xinyirun.scm.core.bpm.mapper.business.notice;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xinyirun.scm.bean.entity.busniess.notice.BNoticeStaffEntity;
import com.xinyirun.scm.bean.system.vo.master.user.MStaffVo;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author xinyirun
 * @since 2024-01-29
 */
@Repository
public interface BBpmNoticeStaffMapper extends BaseMapper<BNoticeStaffEntity> {

    @Select(""
            + "  SELECT                                                                                                 "
            + "     t.name,                                                                                             "
            + "     t.id                                                                                                "
            + "  FROM m_staff t                                                                                         "
            + "  LEFT JOIN b_notice_staff t1 ON t.id = t1.staff_id                                                      "
            + "  WHERE t1.notice_id = #{noticeId}                                                                       "
    )
    List<MStaffVo> selectStaffListByNoticeId(Integer noticeId);
}
