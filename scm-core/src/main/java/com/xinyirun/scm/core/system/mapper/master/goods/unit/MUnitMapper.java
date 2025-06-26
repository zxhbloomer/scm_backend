package com.xinyirun.scm.core.system.mapper.master.goods.unit;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xinyirun.scm.bean.entity.master.goods.unit.MUnitEntity;
import com.xinyirun.scm.bean.system.vo.master.goods.MUnitVo;
import com.xinyirun.scm.common.constant.SystemConstants;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>
 * 单位 Mapper 接口
 * </p>
 *
 * @author htt
 * @since 2021-09-23
 */
@Repository
public interface MUnitMapper extends BaseMapper<MUnitEntity> {

    String common_select = "  "
            + "     SELECT                                                             "
            + "            t.*                                                         "
            + "       FROM                                                             "
            + "  	       m_unit t                                                    "
            + "                                                                        "
            ;

    /**
     * 页面查询列表
     */
    @Select("    "
            + common_select
            + "  where true "
            + "    and (t.name like #{p1.name,jdbcType=VARCHAR} or #{p1.name,jdbcType=VARCHAR} is null) "
            + "      ")
    IPage<MUnitVo> selectPage(Page page, @Param("p1") MUnitVo searchCondition);

    /**
     * 页面查询列表
     */
    @Select("    "
            + common_select
            + "  where true                                                                                "
            + "    and (t.name = #{p1,jdbcType=VARCHAR})                                                   "
            + "    and (t.enable = #{p2,jdbcType=BOOLEAN} or #{p2,jdbcType=BOOLEAN} is null) "
            + "      ")
    MUnitVo selectByName(@Param("p1") String unit, @Param("p2") Boolean enable);

    /**
     * 页面查询列表
     */
    @Select("    "
            + common_select
            + "  where true "
            + "    and (t.name like #{p1.name,jdbcType=VARCHAR} or #{p1.name,jdbcType=VARCHAR} is null) "
            + "    and (t.enable = #{p1.enable,jdbcType=BOOLEAN} or #{p1.enable,jdbcType=BOOLEAN} is null) "
            + "      ")
    List<MUnitVo> selectList(@Param("p1") MUnitVo searchCondition);

    /**
     * 页面查询列表
     */
    @Select("    "
            + common_select
            + "  where true "
            + "    and (t.code = #{p1,jdbcType=VARCHAR} ) "
            + "      ")
    MUnitVo selectByCode(@Param("p1") String code);
}
