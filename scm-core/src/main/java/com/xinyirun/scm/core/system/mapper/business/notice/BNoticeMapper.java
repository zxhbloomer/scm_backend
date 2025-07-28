package com.xinyirun.scm.core.system.mapper.business.notice;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xinyirun.scm.bean.entity.business.wms.inplan.BInPlanEntity;
import com.xinyirun.scm.bean.entity.business.notice.BNoticeEntity;
import com.xinyirun.scm.bean.system.vo.business.notice.BNoticeVo;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>
 * 通知表 Mapper 接口
 * </p>
 *
 * @author xinyirun
 * @since 2024-01-29
 */
@Repository
public interface BNoticeMapper extends BaseMapper<BNoticeEntity> {

    /**
     * 列表查询
     * @param pageCondition
     * @param param
     * @return
     */
    @Select("<script>"
            + " SELECT                                                                                                  "
            + "   t.id,                                                                                                 "
            + "   t.msg,                                                                                                "
            + "   t.c_time,                                                                                             "
            + "   t2.label as type_name,                                                                                "
            + "   t3.label as status_name,                                                                               "
            + "   t4.name as c_name,                                                                                    "
            + "   t1.is_read,                                                                                           "
            + "   t.title                                                                                               "
            + " FROM b_notice t                                                                                         "
            + " LEFT JOIN b_notice_staff t1 ON t.id = t1.notice_id                                                      "
            + " LEFT JOIN v_dict_info t2 ON t2.dict_value = t.type AND t2.code = 'b_notice_type'                        "
            + " LEFT JOIN v_dict_info t3 ON t3.dict_value = t.status AND t3.code = 'b_notice_status'                    "
            + " LEFT JOIN m_staff t4 ON t4.id = t.c_id                                                                  "
            + " WHERE (t.status = #{p1.status} or #{p1.status} is null or #{p1.status} = '')                            "
            + " AND (t1.staff_id = #{p1.staff_id} or #{p1.staff_id} is null or #{p1.staff_id} = '')                     "
            + " AND (t.title like concat('%', #{p1.title}, '%') or #{p1.title} is null or #{p1.title} = '')             "
            + " <if test= 'p1.is_read != null and p1.is_read == \"0\"'>                                                 "
            + " AND (t1.is_read = '0' or t1.is_read is null)                                                           "
            + " </if>                                                                                                   "
            + " <if test= 'p1.is_read != null and p1.is_read == \"1\"'>                                                 "
            + " AND t1.is_read = '1'                                                                                    "
            + " </if>                                                                                                   "
            + " </script>                                                                                               "
    )
    IPage<BNoticeVo> selectPageList(@Param("p2") Page<BInPlanEntity> pageCondition, @Param("p1") BNoticeVo param);

    /**
     * 列表查询
     * @param param
     * @return
     */
    @Select("       "
            + " SELECT                                                                                                  "
            + "   t.id,                                                                                                 "
            + "   t.msg,                                                                                                "
            + "   t.c_time,                                                                                             "
            + "   t2.label as type_name,                                                                                "
            + "   t3.label as status_name,                                                                               "
            + "   t4.name as c_name,                                                                                    "
            + "   t1.is_read,                                                                                           "
            + "   t.title                                                                                               "
            + " FROM b_notice t                                                                                         "
            + " LEFT JOIN b_notice_staff t1 ON t.id = t1.notice_id                                                      "
            + " LEFT JOIN v_dict_info t2 ON t2.dict_value = t.type AND t2.code = 'b_notice_type'                        "
            + " LEFT JOIN v_dict_info t3 ON t3.dict_value = t.status AND t3.code = 'b_notice_status'                    "
            + " LEFT JOIN m_staff t4 ON t4.id = t.c_id                                                                  "
            + " WHERE (t.status = #{p1.status} or #{p1.status} is null or #{p1.status} = '')                            "
            + " AND (t1.staff_id = #{p1.staff_id} or #{p1.staff_id} is null or #{p1.staff_id} = '')                     "
            + " AND t1.is_read is null                                                                                   "
            + " order by t.c_time desc                                                                                  "
            + " limit 10                                                                                                "
    )
    List<BNoticeVo> getNoticeUnreadTen(@Param("p1") BNoticeVo param);

    /**
     * 查询 详情
     * @return
     */
    @Select(""
            + " SELECT                                                                                                  "
            + "   t.id,                                                                                                 "
            + "   t.msg,                                                                                                "
            + "   t.c_time,                                                                                             "
            + "   t1.is_read,                                                                                           "
            + "   t.title                                                                                               "
            + " FROM b_notice t                                                                                         "
            + " LEFT JOIN b_notice_staff t1 ON t.id = t1.notice_id                                                      "
            + " WHERE (t.status = #{p1.status} or #{p1.status} is null or #{p1.status} = '')                            "
            + " AND (t1.staff_id = #{p1.staff_id} or #{p1.staff_id} is null or #{p1.staff_id} = '')                     "
            + " AND t.id = #{p1.id}                                                                                     "
    )
    BNoticeVo selectPCDetail(@Param("p1") BNoticeVo param);
}
