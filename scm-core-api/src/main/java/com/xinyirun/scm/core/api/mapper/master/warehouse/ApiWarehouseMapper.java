package com.xinyirun.scm.core.api.mapper.master.warehouse;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xinyirun.scm.bean.entity.master.warehouse.MWarehouseEntity;
import com.xinyirun.scm.bean.api.vo.master.warehouse.ApiWarehouseVo;
import com.xinyirun.scm.bean.system.vo.master.warhouse.MWarehouseVo;
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
public interface ApiWarehouseMapper extends BaseMapper<MWarehouseEntity> {

    String common_select = "  "
            + "     SELECT                                                             "
            + "            t.*,                                                        "
            + "            concat(t.province,t.city,t.district) cascader_areas,          "
            + "            t1.name as c_name,                                          "
            + "            t2.name as u_name                                           "
            + "       FROM                                                             "
            + "  	       m_warehouse t                                                  "
            + "  LEFT JOIN m_staff t1 ON t.c_id = t1.id                                 "
            + "  LEFT JOIN m_staff t2 ON t.u_id = t2.id                                 "
            ;

    /**
     * 页面查询列表
     * @param page
     * @param searchCondition
     * @return
     */
    @Select("    "
            + common_select
            + "  where true "
            + "    and (t.operate_company_id = #{p1.operate_company_id,jdbcType=INTEGER} or #{p1.operate_company_id,jdbcType=INTEGER} is null) "
            + "    and (t.operate_company_id = #{p1.charge_company_id,jdbcType=INTEGER} or #{p1.charge_company_id,jdbcType=INTEGER} is null) "
            + "    and (t.enable = #{p1.enable,jdbcType=BOOLEAN} or #{p1.enable,jdbcType=BOOLEAN} is null) "
            + "    and (t.name  = #{p1.name,jdbcType=VARCHAR}  or #{p1.name,jdbcType=VARCHAR} is null) "
            + "    and (t.code  = #{p1.code,jdbcType=VARCHAR}  or #{p1.code,jdbcType=VARCHAR} is null) "
            + "      ")
    IPage<MWarehouseVo> selectPage(Page page, @Param("p1") MWarehouseVo searchCondition);

    /**
     * 页面查询列表
     * @param searchCondition
     * @return
     */
    @Select("    "
            + common_select
            + "  where true "
            + "    and (t.operate_company_id = #{p1.operate_company_id,jdbcType=INTEGER} or #{p1.operate_company_id,jdbcType=INTEGER} is null) "
            + "    and (t.operate_company_id = #{p1.charge_company_id,jdbcType=INTEGER} or #{p1.charge_company_id,jdbcType=INTEGER} is null) "
            + "    and (t.enable = #{p1.enable,jdbcType=BOOLEAN} or #{p1.enable,jdbcType=BOOLEAN} is null) "
            + "    and (t.name  = #{p1.name,jdbcType=VARCHAR}  or #{p1.name,jdbcType=VARCHAR} is null) "
            + "    and (t.code  = #{p1.code,jdbcType=VARCHAR}  or #{p1.code,jdbcType=VARCHAR} is null) "
            + "      ")
    List<MWarehouseVo> selectList(@Param("p1") MWarehouseVo searchCondition);

    /**
     * 仓库下拉
     * @param vo
     * @return
     */
    @Select("    "
            + common_select
            + "  where true                                                              "
            + "    and (t.enable = true)                                                                    "
            + "    and (t.code = #{p1.code,jdbcType=VARCHAR} or #{p1.code,jdbcType=VARCHAR} is null) "
            + "    and (t.name  = #{p1.name,jdbcType=VARCHAR}  or #{p1.name,jdbcType=VARCHAR} is null) "
            + "    and (t.short_name  = #{p1.short_name,jdbcType=VARCHAR}  or #{p1.short_name,jdbcType=VARCHAR} is null) "
            + "    and (t.business_type  = #{p1.business_type,jdbcType=VARCHAR}  or #{p1.business_type,jdbcType=VARCHAR} is null) "
            + "      ")
    List<ApiWarehouseVo> getWarehouse(@Param("p1") ApiWarehouseVo vo);

}
