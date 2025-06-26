package com.xinyirun.scm.core.system.mapper.query.inventory;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xinyirun.scm.bean.entity.master.inventory.MInventoryEntity;
import com.xinyirun.scm.bean.system.vo.master.inventory.MInventoryVo;
import com.xinyirun.scm.bean.system.vo.master.inventory.query.MInventoryDetailQueryVo;
import com.xinyirun.scm.bean.system.vo.master.inventory.query.MInventoryOwnerGoodsQueryVo;
import com.xinyirun.scm.common.constant.DictConstant;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>
 * 库存表 Mapper 接口
 * </p>
 *
 * @author htt
 * @since 2021-09-23
 */
@Repository
public interface MInventoryOwnerGoodsQueryMapper extends BaseMapper<MInventoryEntity> {

    String select_query_detail = "  "
            + "     select tt.*                                                                               "
            + "       from (                                                                                  "
            + "           select tab1.business_name ,                                                         "
            + "                  tab1.industry_name,                                                          "
            + "                  tab1.category_name,                                                          "
            + "                  tab1.business_id,                                                            "
            + "                  tab1.industry_id,                                                            "
            + "                  tab1.cateory_id,                                                             "
            + "                  tab1.owner_id,                                                               "
            + "                  tab1.owner_code,                                                             "
            + "                  tab1.owner_name,                                                             "
            + "                  tab1.owner_short_name,                                                       "
            + "                  tab1.goods_code,                                                             "
            + "                  tab1.sku_name,                                                               "
            + "                  tab1.unit_id,                                                                "
            + "                  tab1.unit_name,                                                              "
            + "                  sum(tab1.qty_avaible) as qty_avaible,                                        "
            + "                  sum(tab1.qty_lock) as qty_lock,                                              "
            + "                  sum(tab1.amount) as amount,                                                  "
            + "                  sum(tab1.amount) / (sum(tab1.qty_avaible) + sum(tab1.qty_lock)) as price     "
            + "                                                                                        "
            + "             from (                                                                     "
            + "                    select t1.id,                                                       "
            + "                           t12.id as business_id,                                       "
            + "                           t12.name business_name,                                      "
            + "                           t11.id as industry_id,                                       "
            + "                           t11.name industry_name,                                      "
            + "                           t10.id as cateory_id,                                        "
            + "                           t10.name as category_name,                                   "
            + "                           t1.code as inventory_code,                                   "
            + "                           t1.warehouse_id ,                                            "
            + "                           t1.location_id ,                                             "
            + "                           t1.bin_id ,                                                  "
            + "                           t2.name as warehouse_name,                                   "
            + "                           t2.short_name as warehouse_short_name,                       "
            + "                           t3.name as location_name,                                    "
            + "                           t3.short_name as location_short_name,                        "
            + "                           t4.name as bin_name,                                         "
            + "                           t7.id as owner_id,                                           "
            + "                           t7.code as owner_code,                                       "
            + "                           t7.name as owner_name,                                       "
            + "                           t7.short_name as owner_short_name,                           "
            + "                           t1.sku_id ,                                                  "
            + "                           t1.sku_code ,                                                "
            + "                           t6.name as sku_name,                                         "
            + "                           t6.spec ,                                                    "
            + "                           t6.goods_code ,                                              "
            + "                           t6.goods_id ,                                                "
            + "                           t6.prop_id ,                                                 "
            + "                           t6.pm,                                                       "
            + "                           t1.unit_id ,                                                 "
            + "                           t5.name as unit_name,                                        "
            + "                           t1.lot,                                                      "
            + "                           t1.qty_avaible,                                              "
            + "                           t1.qty_lock,                                                 "
            + "                           t1.price,                                                    "
            + "                           t1.amount,                                                   "
            + "                           t1.u_time                                                    "
            + "                      from m_inventory t1                                               "
            + "                 left join m_warehouse t2 on t1.warehouse_id = t2.id                    "
            + "                 left join m_location t3 on t1.location_id = t3.id                      "
            + "                 left join m_bin t4 on t1.bin_id = t4.id                                "
            + "                 left join s_unit t5 on t1.unit_id = t5.id                              "
            + "                 left join m_goods_spec t6 on t1.sku_id = t6.id                         "
            + "                 left join m_owner t7 on t1.owner_id = t7.id                            "
            + "                 left join m_staff t8 on t1.u_id = t8.id                                "
            + "                 left join m_goods t9 on t6.goods_id = t9.id                            "
            + "                 left join m_category t10 on t9.category_id = t10.id                    "
            + "                 left join m_industry t11 on t10.industry_id = t11.id                   "
            + "                 left join m_business_type t12 on t11.business_id = t12.id              "
            + "           ) tab1                                                                       "
            + "           group by tab1.business_id,                                                   "
            + "                    tab1.industry_id,                                                   "
            + "                    tab1.cateory_id,                                                    "
            + "                    tab1.owner_id,                                                      "
            + "                    tab1.sku_name                                                       "
            + "        ) tt                                                                            "
            + "              "
            ;

    /**
     * 库存明细查询sql
     */
    @Select("   "
            + select_query_detail
            + "  where true                                                                                                                  "
            + "    and (tt.sku_name like CONCAT ('%',#{p1.sku_name,jdbcType=VARCHAR},'%') or #{p1.sku_name,jdbcType=VARCHAR} is null)        "
            + "    and (CONCAT(tt.owner_code,tt.owner_name,tt.owner_short_name) like CONCAT ('%',#{p1.owner_name,jdbcType=VARCHAR},'%') or #{p1.owner_name,jdbcType=VARCHAR} is null)        "
            + "    ")
    IPage<MInventoryOwnerGoodsQueryVo> queryInventoryOwnerGoods(Page page, @Param("p1") MInventoryOwnerGoodsQueryVo searchCondition);

}
