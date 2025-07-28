package com.xinyirun.scm.core.system.mapper.business.check;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xinyirun.scm.bean.entity.business.check.BCheckOperateDetailEntity;
import com.xinyirun.scm.bean.system.vo.business.check.BCheckOperateDetailVo;
import com.xinyirun.scm.bean.system.vo.business.check.BCheckOperateVo;
import com.xinyirun.scm.common.constant.SystemConstants;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>
 * 盘点操作明细 Mapper 接口
 * </p>
 *
 * @author wwl
 * @since 2021-12-29
 */
@Repository
public interface BCheckOperateDetailMapper extends BaseMapper<BCheckOperateDetailEntity> {

    /**
     * 页面查询列表
     */
    @Select("    "
            + " select * from b_check_operate_detail                "
            + "   where true                                        "
            + "   and check_operate_id = #{p1,jdbcType=INTEGER}     "
            + "       ")
    List<BCheckOperateDetailEntity> selectListById(@Param("p1") int check_operate_id);

    /**
     * 查询列表
     */
    @Select("    "
            + "SELECT  t1.*,                                                                                    "
            + "        t2.code sku_code,                                                                        "
            + "        t2.spec sku_name,                                                                        "
            + "        t2.name goods_name,                                                                      "
            + "        t2.pm pm,                                                                                "
            + "        '"+ SystemConstants.INVENTORY_UNIT +"' as unit                                           "
            + "       from b_check_operate_detail t1                                                            "
            + "       LEFT JOIN m_goods_spec t2 ON t1.sku_id = t2.id                           					"
            + "  where true                                                                                     "
            + "    and t1.check_operate_id =  #{p1.id}                                                          "
            + "      ")
    List<BCheckOperateDetailVo> selectList(@Param("p1") BCheckOperateVo searchCondition);

}
