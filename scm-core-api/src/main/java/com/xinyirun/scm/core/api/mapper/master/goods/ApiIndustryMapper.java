package com.xinyirun.scm.core.api.mapper.master.goods;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xinyirun.scm.bean.entity.master.goods.MIndustryEntity;
import com.xinyirun.scm.bean.api.vo.master.goods.ApiIndustryVo;
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
public interface ApiIndustryMapper extends BaseMapper<MIndustryEntity> {

    String common_select = "  "
            + "     SELECT                                                             "
            + "            t.*,                                                        "
            + "            t3.name as business_name,                                          "
            + "            t1.name as c_name,                                          "
            + "            t2.name as u_name                                           "
            + "       FROM                                                             "
            + "  	       m_industry t                                                  "
            + "  LEFT JOIN m_staff t1 ON t.c_id = t1.id                                 "
            + "  LEFT JOIN m_staff t2 ON t.u_id = t2.id                                 "
            + "  LEFT JOIN m_business_type t3 ON t3.id = t.business_id                                 "
            + "                                                                        "
            ;

    /**
     * code、来源查询数据
     * @param searchCondition
     * @return
     */
    @Select("    "
            + common_select
            + "  where true "
            + "    and (t.code = #{p1.code,jdbcType=VARCHAR} ) "
            + "      ")
    MIndustryEntity selectByCodeAppCode(@Param("p1") ApiIndustryVo searchCondition);

    /**
     * code、板块code、来源、name查询数据
     * @param searchCondition
     * @return
     */
    @Select("    "
            + common_select
            + "  where true "
            + "    and (t.name = #{p1.name,jdbcType=VARCHAR} ) "
            + "    and (t.business_type_code = #{p1.business_type_code,jdbcType=VARCHAR} ) "
            + "      ")
    List<MIndustryEntity> selectByAppCodeBusinessTypeCodeName(@Param("p1") ApiIndustryVo searchCondition);

}
