package com.xinyirun.scm.core.api.mapper.master.customer;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xinyirun.scm.bean.entity.master.customer.MOwnerEntity;
import com.xinyirun.scm.bean.api.vo.master.customer.ApiCustomerVo;
import com.xinyirun.scm.bean.api.vo.master.customer.ApiOwnerVo;
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
 * @since 2021-10-27
 */
@Repository
public interface ApiOwnerMapper extends BaseMapper<MOwnerEntity> {

    String common_select = "  "
            + "     SELECT                                                             "
            + "            t.*,                                                        "
            + "            t1.name as c_name,                                          "
            + "            t2.name as u_name                                           "
            + "       FROM                                                             "
            + "  	       m_owner t                                                  "
            + "  LEFT JOIN m_staff t1 ON t.c_id = t1.id                                 "
            + "  LEFT JOIN m_staff t2 ON t.u_id = t2.id                                 "
            + "                                                                        "
            ;


    /**
     * 按条件获取所有数据，没有分页
     * @param
     * @return
     */
    @Select("    "
            + common_select
            + "  where t.code =  #{p1.code,jdbcType=VARCHAR}"
            + "      ")
    MOwnerEntity selectByCodeAppCode(@Param("p1") ApiCustomerVo vo);

    /**
     * 按条件获取所有数据，没有分页
     * @param
     * @return
     */
    @Select("    "
            + common_select
            + "  where t.credit_no =  #{p1.credit_no,jdbcType=VARCHAR}"
            + "      ")
    MOwnerEntity selectByCreditNo(@Param("p1") ApiCustomerVo vo);

    /**
     * 按条件获取所有数据，没有分页
     * @param
     * @return
     */
    @Select("    "
            + common_select
            + "  where t.name =  #{p1,jdbcType=VARCHAR}"
            + "      ")
    List<MOwnerEntity> selectListByName(@Param("p1") String name);

    /**
     * 货主下拉
     * @param vo
     */
    @Select("    "
            + common_select
            + "  where true "
            + "    and (t.code = #{p1.code,jdbcType=VARCHAR} or #{p1.code,jdbcType=VARCHAR} is null) "
            + "    and (t.name  = #{p1.name,jdbcType=VARCHAR}  or #{p1.name,jdbcType=VARCHAR} is null) "
            + "    and (t.short_name  = #{p1.short_name,jdbcType=VARCHAR}  or #{p1.short_name,jdbcType=VARCHAR} is null) "
            + "    and (t.business_type  = #{p1.business_type,jdbcType=VARCHAR}  or #{p1.business_type,jdbcType=VARCHAR} is null) "
            + "      ")
    List<ApiOwnerVo> getOwner(@Param("p1") ApiOwnerVo vo);
}
