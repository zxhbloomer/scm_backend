package com.xinyirun.scm.core.system.mapper.business.ownerchange;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xinyirun.scm.bean.entity.busniess.ownerchange.BOwnerChangeDetailEntity;
import com.xinyirun.scm.bean.system.vo.business.ownerchange.BOwnerChangeDetailVo;
import com.xinyirun.scm.bean.system.vo.business.ownerchange.BOwnerChangeVo;
import com.xinyirun.scm.common.constant.DictConstant;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>
 * 库存调整 Mapper 接口
 * </p>
 *
 * @author wwl
 * @since 2021-12-09
 */
@Repository
public interface BOwnerChangeDetailMapper extends BaseMapper<BOwnerChangeDetailEntity> {

    String common_select = "                                                                             "
            + "     SELECT                                                                               "
            + "            t.id,                                                                         "
            + "            t.qty,                                                                        "
            + "            t4.code,                                                                      "
            + "            t4.out_owner_id owner_id,                                                     "
            + "            t5.code sku_code,                                                             "
            + "            t5.code spec_code,                                                            "
            + "            t6.name sku_name,                                                             "
            + "            t5.spec,                                                                      "
            + "            t5.pm                                                                         "
            + "       FROM                                                                               "
            + "  	       b_owner_change_detail t                                                       "
            + "  LEFT JOIN b_owner_change t4 ON t.owner_change_id = t4.id                                "
            + "  LEFT JOIN m_goods_spec t5 ON t.sku_id = t5.id                                           "
            + "  LEFT JOIN m_goods t6 ON t5.goods_id = t6.id                                             "
            + "                                                                                          "
            ;

    /**
     * 查看页面查询列表
     */
    @Select("    "
            + common_select
            + "  where true                                                                                       "
            + "    and (t.id =  #{p1.id,jdbcType=INTEGER} )                                                      "
            + "      ")
    List<BOwnerChangeDetailVo> getOwnerChangeDetailList(@Param("p1") BOwnerChangeVo searchCondition);

    /**
     * 删除状态为制单和驳回的明细数据
     */
    @Select("    "
            + "  delete from b_owner_change_detail                                                                                             "
            + "  where owner_change_id = #{p1,jdbcType=INTEGER}                                                                                "
            + "  and status in ( '" + DictConstant.DICT_B_OWNER_CHANGE_STATUS_SAVED +"','" +DictConstant.DICT_B_OWNER_CHANGE_STATUS_RETURN +"' )    ")
    void statusDelete(@Param("p1") int owner_change_id);
}
