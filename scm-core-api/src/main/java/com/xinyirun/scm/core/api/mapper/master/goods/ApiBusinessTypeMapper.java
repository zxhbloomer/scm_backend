package com.xinyirun.scm.core.api.mapper.master.goods;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xinyirun.scm.bean.entity.master.goods.MBusinessTypeEntity;
import com.xinyirun.scm.bean.api.vo.master.goods.ApiBusinessTypeVo;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author htt
 * @since 2021-09-23
 */
@Repository
public interface ApiBusinessTypeMapper extends BaseMapper<MBusinessTypeEntity> {
    String common_select = "  "
            + "     SELECT                                                             "
            + "            t.*,                                                        "
            + "            t1.name as c_name,                                          "
            + "            t2.name as u_name                                           "
            + "       FROM                                                             "
            + "  	       m_business_type t                                                  "
            + "  LEFT JOIN m_staff t1 ON t.c_id = t1.id                                 "
            + "  LEFT JOIN m_staff t2 ON t.u_id = t2.id                                 "
            ;

    /**
     * 来源跟code查询数据
     * @param searchCondition
     * @return
     */
    @Select("    "
            + common_select
            + "  where true "
            + "    and (t.code = #{p1.code,jdbcType=VARCHAR} ) "
            + "      ")
    MBusinessTypeEntity selectByCodeAppCode(@Param("p1") ApiBusinessTypeVo searchCondition);

    /**
     * 来源、name查询数据
     * @param searchCondition
     * @return
     */
    @Select("    "
            + common_select
            + "  where true "
            + "    and (t.name = #{p1.name,jdbcType=VARCHAR}) "
            + "      ")
    List<MBusinessTypeEntity> selectByAppCodeName(@Param("p1") ApiBusinessTypeVo searchCondition);


}
