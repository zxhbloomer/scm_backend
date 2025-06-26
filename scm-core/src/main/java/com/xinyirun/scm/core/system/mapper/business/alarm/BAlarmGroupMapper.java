package com.xinyirun.scm.core.system.mapper.business.alarm;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xinyirun.scm.bean.entity.busniess.alarm.BAlarmGroupEntity;
import com.xinyirun.scm.bean.system.vo.business.alarm.BAlarmGroupVo;
import com.xinyirun.scm.bean.system.vo.business.alarm.BAlarmStaffTransferVo;
import com.xinyirun.scm.common.constant.DictConstant;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>
 * 仓库组一级分类 Mapper 接口
 * </p>
 *
 * @author xinyirun
 * @since 2022-12-16
 */
@Repository
public interface BAlarmGroupMapper extends BaseMapper<BAlarmGroupEntity> {

    /**
     * 预警组分页查询
     * @param page 分页参数
     * @param vo 查询参数
     * @return IPage<BAlarmGroupVo>
     */
    @Select(""
            +  "  SELECT                                                                                               "
            +  "    t.id,                                                                                              "
            +  "  	t.`name`,                                                                                          "
            +  "  	t.`code`,                                                                                          "
            +  "  	t.short_name,                                                                                      "
            +  "  	t.name_pinyin,                                                                                     "
            +  "  	t.short_name_pinyin,                                                                               "
            +  "  	t.name_pinyin_abbr,                                                                                "
            +  "  	t.short_name_pinyin_abbr,                                                                          "
            +  "  	t4.staff_count,                                                                                    "
            +  "  	t.c_time,                                                                                          "
            +  "  	t.u_time,                                                                                          "
            +  "    t1.`name` c_name,                                                                                  "
            +  "  	t2.`name` u_name                                                                                   "
            +  "  FROM                                                                                                 "
            +  "    b_alarm_group t                                                                                    "
            +  "  LEFT JOIN m_staff t1 ON t.c_id = t1.id                                                               "
            +  "  LEFT JOIN m_staff t2 ON t.u_id = t2.id                                                               "
            +  "  LEFT JOIN (                                                                                          "
            +  "      SELECT count(1) as staff_count, t3.alarm_group_id FROM                                           "
            +  "         b_alarm_group_staff t3                                                                        "
            +  "       group by t3.alarm_group_id                                                                      "
            +  "  ) t4 ON t4.alarm_group_id = t.id                                                                     "
            +  "  WHERE TRUE                                                                                           "
            +  "  AND (CONCAT(IFNULL(t.`name`, ''), '_', IFNULL(t.`code`, '')) LIKE CONCAT('%', #{p1.name}, '%') OR #{p1.name} is null or #{p1.name} = '')"
    )
    IPage<BAlarmGroupVo> selectPageList(Page<BAlarmGroupVo> page,@Param("p1") BAlarmGroupVo vo);

    /**
     * 根据 ID 查询
     * @param id 主键 ID
     * @return BAlarmGroupVo
     */
    @Select(""
            +  "  SELECT                                                                                                "
            +  "    t.id,                                                                                               "
            +  "  	t.`code`,                                                                                           "
            +  "  	t.`name`,                                                                                           "
            +  "  	t.c_time,                                                                                           "
            +  "  	t.u_time,                                                                                           "
            +  "  	t.dbversion,                                                                                        "
            +  "  	t.short_name,                                                                                       "
            +  "  	t1.`name` c_name,                                                                                   "
            +  "  	t2.`name` u_name                                                                                    "
            +  "  FROM                                                                                                  "
            +  "    b_alarm_group t                                                                                     "
            +  "  LEFT JOIN m_staff t1 ON t.c_id = t1.id                                                                "
            +  "  LEFT JOIN m_staff t2 ON t.u_id = t2.id                                                                "
            +  "  WHERE t.id = #{p1}                                                                                    "
    )
    BAlarmGroupVo selectVoById(@Param("p1") Integer id);

    @Select("                                                                                                           "
            + "     SELECT                                                                                              "
            + "             t1.id AS `key`,                                                                             "
            + "             t1.NAME AS label                                                                            "
            + "       FROM  m_staff t1                                                                                  "
            + "      WHERE                                                                                              "
            + "             t1.is_del = "+ DictConstant.DICT_SYS_DELETE_MAP_NO+"                                        "
            + "   order by  t1.id                                                                                       "
    )
    List<BAlarmStaffTransferVo> getAllStaffTransferList();

    @Select(""
            +  "  SELECT                                                                                                "
            +  "    t.staff_id                                                                                          "
            +  "  FROM                                                                                                  "
            +  "    b_alarm_group_staff t                                                                               "
            +  "  WHERE t.alarm_group_id = #{p1}                                                                        "
            + "   order by  t.staff_id                                                                                  "
    )
    List<Long> getUsedStaffTransferList(@Param("p1") Integer group_id );

}
