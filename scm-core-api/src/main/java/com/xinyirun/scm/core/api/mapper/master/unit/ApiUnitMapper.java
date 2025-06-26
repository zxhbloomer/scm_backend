package com.xinyirun.scm.core.api.mapper.master.unit;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xinyirun.scm.bean.entity.master.goods.MGoodsEntity;
import com.xinyirun.scm.bean.entity.master.goods.unit.MUnitEntity;
import com.xinyirun.scm.bean.api.vo.master.unit.ApiUnitVo;
import com.xinyirun.scm.bean.entity.sys.unit.SUnitEntity;
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
public interface ApiUnitMapper extends BaseMapper<MUnitEntity> {
    String common_select = "  "
            + "     SELECT                                                             "
            + "            t.*                                                        "
            + "       FROM                                                             "
            + "  	       m_unit t                                                  "
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
    MUnitEntity selectByCodeAppCode(@Param("p1") ApiUnitVo searchCondition);

    /**
     * category_code、name、来源查询数据
     * @param searchCondition
     * @return
     */
    @Select(common_select
            + "  where true "
            + "    and (t.code = #{p1.category_code,jdbcType=VARCHAR} ) "
            + "    and (t.name = #{p1.name,jdbcType=VARCHAR} ) "
            + "      ")
    List<MGoodsEntity> selectByAppCodeCategoryCodeName(@Param("p1") ApiUnitVo searchCondition);

    @Select(""
            + "     SELECT                                                             "
            + "            t.*                                                         "
            + "       FROM                                                             "
            + "  	       s_unit t                                                    "
            + "  where true                                                            "
            + "    and (t.code = #{p1,jdbcType=VARCHAR} )                              "
            + "      ")
    SUnitEntity selectSUnitOne(@Param("p1") String code);
}
