package com.xinyirun.scm.core.system.mapper.master.goods;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xinyirun.scm.bean.entity.master.goods.MGoodsSpecPropEntity;
import com.xinyirun.scm.bean.system.vo.master.goods.MGoodsSpecPropVo;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author htt
 * @since 2021-09-23
 */
@Repository
public interface MGoodsSpecPropMapper extends BaseMapper<MGoodsSpecPropEntity> {


    /**
     * 按code查询
     * @param code
     * @return
     */
    @Select("    "
            + "  select * from m_goods_spec_prop t"
            + "  where t.code =  #{p1,jdbcType=VARCHAR}"
            + "      ")
    MGoodsSpecPropVo selectByCode(@Param("p1") String code);
}
