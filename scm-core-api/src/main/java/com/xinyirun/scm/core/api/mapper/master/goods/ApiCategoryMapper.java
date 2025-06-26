package com.xinyirun.scm.core.api.mapper.master.goods;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xinyirun.scm.bean.entity.master.goods.MCategoryEntity;
import com.xinyirun.scm.bean.api.vo.master.goods.ApiCategoryVo;
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
public interface ApiCategoryMapper extends BaseMapper<MCategoryEntity> {
    String common_select = "  "
            + "     SELECT                                                             "
            + "            t.*,                                                        "
            + "            t3.id as business_id,                                          "
            + "            t3.name as business_name,                                          "
            + "            t4.name as industry_name,                                          "
            + "            t1.name as c_name,                                          "
            + "            t2.name as u_name                                           "
            + "       FROM                                                             "
            + "  	       m_category t                                                  "
            + "  LEFT JOIN m_staff t1 ON t.c_id = t1.id                                 "
            + "  LEFT JOIN m_staff t2 ON t.u_id = t2.id                                 "
            + "  LEFT JOIN m_industry t4 ON t4.id = t.industry_id                                 "
            + "  LEFT JOIN m_business_type t3 ON t4.business_id = t3.id                                 "
            ;

    /**
     * code、来源查询数据
     * @param searchCondition
     * @return
     */
    @Select(common_select
            + "  where true "
            + "    and (t.code = #{p1.code,jdbcType=VARCHAR} ) "
            + "      ")
    MCategoryEntity selectByCodeAppCode(@Param("p1") ApiCategoryVo searchCondition);

    /**
     * Industry_code、name、来源查询数据
     * @param searchCondition
     * @return
     */
    @Select(common_select
            + "  where true "
            + "    and (t.industry_code = #{p1.industry_code,jdbcType=VARCHAR} ) "
            + "    and (t.name = #{p1.name,jdbcType=VARCHAR} ) "
            + "      ")
    List<MCategoryEntity> selectByAppCodeIndustryCodeName(@Param("p1") ApiCategoryVo searchCondition);

}
