package com.xinyirun.scm.core.system.mapper.sys.unit;

import com.xinyirun.scm.bean.entity.sys.unit.SUnitEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xinyirun.scm.bean.system.vo.master.goods.MUnitVo;
import com.xinyirun.scm.bean.system.vo.sys.unit.SUnitVo;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

/**
 * <p>
 * 单位 Mapper 接口
 * </p>
 *
 * @author xinyirun
 * @since 2022-02-03
 */
@Repository
public interface SUnitMapper extends BaseMapper<SUnitEntity> {

    String common_select = "  "
            + "     SELECT                                                             "
            + "            t.*                                                         "
            + "       FROM                                                             "
            + "  	       s_unit t                                                    "
            + "                                                                        ";

    /**
     * 页面查询列表
     */
    @Select("    "
            + common_select
            + "  where true "
            + "    and (t.code = #{p1,jdbcType=VARCHAR} ) "
            + "      ")
    SUnitVo selectByCode(@Param("p1") String code);
}
