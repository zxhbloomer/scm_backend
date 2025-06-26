package com.xinyirun.scm.core.api.mapper.master.goods;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xinyirun.scm.bean.entity.master.goods.MGoodsSpecEntity;
import com.xinyirun.scm.bean.api.vo.master.goods.ApiGoodsSpecVo;
import com.xinyirun.scm.bean.entity.sys.config.config.SConfigEntity;
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
public interface ApiGoodsSpecMapper extends BaseMapper<MGoodsSpecEntity> {
    String common_select = "  "
            + "     SELECT                                                             "
            + "            t.*,                                                        "
            + "            t6.id as goods_id,                                          "
            + "            t6.name as goods_name,                                          "
            + "            t3.id as business_id,                                          "
            + "            t4.id as industry_id,                                          "
            + "            t5.id as category_id,                                          "
            + "            t3.name as business_name,                                          "
            + "            t4.name as industry_name,                                          "
            + "            t5.name as category_name,                                          "
            + "            t1.name as c_name,                                          "
            + "            t2.name as u_name                                           "
            + "       FROM                                                             "
            + "  	       m_goods_spec t                                                  "
            + "  LEFT JOIN m_staff t1 ON t.c_id = t1.id                                 "
            + "  LEFT JOIN m_staff t2 ON t.u_id = t2.id                                 "
            + "  LEFT JOIN m_goods t6 ON t.goods_id = t6.id                "
            + "  LEFT JOIN m_category t5 ON t6.category_id = t5.id                "
            + "  LEFT JOIN m_industry t4 ON t4.id = t5.industry_id                                 "
            + "  LEFT JOIN m_business_type t3 ON t4.business_id = t3.id                "
            + "                                                                        "
            ;


    /**
     * 按规格编码和来源获取数据
     * @param vo
     * @return
     */
    @Select("    "
            + common_select
            + "  where  t.code =  #{p1.code,jdbcType=VARCHAR}"
            + "      ")
    MGoodsSpecEntity selectByCodeAppCode(@Param("p1") ApiGoodsSpecVo vo);

    /**
     * 按来源\商品编号\规格名称获取数据
     * @param vo
     * @return
     */
    @Select("    "
            + common_select
            + "  where  true                                                   "
            + "         and t.goods_code =  #{p1.goods_code,jdbcType=VARCHAR}  "
            + "         and t.spec =  #{p1.spec,jdbcType=VARCHAR}              "
            + "      ")
    List<MGoodsSpecEntity> selectByAppCodeGoodsCodeSpec(@Param("p1") ApiGoodsSpecVo vo);

    /**
     * 查询配置
     * @param defaultGoodsUnitCalc
     * @return
     */
    @Select("    "
            + "     SELECT                                                                                              "
            + "            t.*                                                                                          "
            + "       FROM                                                                                              "
            + "  	       s_config t                                                                                   "
            + "  where true                                                                                             "
            + "    and t.config_key =  #{p1}                                                                            "
            + "      ")
    SConfigEntity selectConfigByKey(@Param("p1") String defaultGoodsUnitCalc);
}
