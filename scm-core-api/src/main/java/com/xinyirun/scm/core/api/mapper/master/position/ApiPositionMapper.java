package com.xinyirun.scm.core.api.mapper.master.position;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xinyirun.scm.bean.api.vo.business.position.ApiPositionVo;
import com.xinyirun.scm.bean.entity.master.org.MPositionEntity;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>
 * 岗位主表 Mapper 接口
 * </p>
 *
 * @author zxh
 * @since 2019-08-23
 */
@Repository
public interface ApiPositionMapper extends BaseMapper<MPositionEntity> {

    /**
     * 页面查询列表
     * @param searchCondition
     * @return
     */
    @Select("    "
        + "        SELECT                                                                                               "
        + "               t1.*                                                                                          "
        + "          FROM m_position t1                                                                                 "
        + "  where true                                                                                                 "
        + "    and t1.is_del = false                                                                                    "
        + "    and (t1.code like CONCAT ('%',#{p1.code,jdbcType=VARCHAR},'%') or #{p1.code,jdbcType=VARCHAR} is null)   "
        + "    and (t1.name like CONCAT ('%',#{p1.name,jdbcType=VARCHAR},'%') or #{p1.name,jdbcType=VARCHAR} is null)   "
        + "      ")
    List<ApiPositionVo> list(@Param("p1") ApiPositionVo searchCondition);



}
