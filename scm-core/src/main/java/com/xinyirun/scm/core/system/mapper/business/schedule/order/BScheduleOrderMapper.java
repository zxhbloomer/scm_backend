package com.xinyirun.scm.core.system.mapper.business.schedule.order;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xinyirun.scm.bean.entity.busniess.schedule.BScheduleOrderEntity;
import com.xinyirun.scm.bean.system.vo.business.schedule.BScheduleOrderVo;
import com.xinyirun.scm.common.constant.DictConstant;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author wwl
 * @since 2022-01-10
 */
@Repository
public interface BScheduleOrderMapper extends BaseMapper<BScheduleOrderEntity> {

    String common_select = "  "
            + "      SELECT                                                                                                                        "
            + "             t.*,                                                                                                                   "
            + "             ifnull(t3.short_name,t3.name) as client_name,                                                                          "
            + "             t5.label as bill_type_name ,                                                                                           "
            + "             t1.name as c_name,                                                                                                     "
            + "             t2.name as u_name                                                                                                      "
            + "        FROM                                                                                                                        "
            + "   	       b_schedule_order t                                                                                                      "
            + "   LEFT JOIN m_staff t1 ON t.c_id = t1.id                                                                                           "
            + "   LEFT JOIN m_staff t2 ON t.u_id = t2.id                                                                                           "
            + "   LEFT JOIN m_customer t3 ON t.client_id = t3.id                                                                                   "
            + "   LEFT JOIN (select tab1.* from s_dict_data tab1 inner join s_dict_type tab2 on tab1.dict_type_id = tab2.id                        "
            + "              where tab2.code = '" + DictConstant.DICT_B_OUT_PLAN_BUSINESS_TYPE + "')t5 on t5.dict_value = t.bill_type              "
            + "   LEFT JOIN m_customer t6 ON t.supplier_id = t6.id                                                                                   "
            + "                                                                        "
            ;


    /**
     * 页面查询列表
     */
    @Select("    "
            + common_select
            + "  where true                                                                                                                       "
            + "    and (t.order_no like CONCAT ('%',#{p1.order_no,jdbcType=VARCHAR},'%') or #{p1.order_no,jdbcType=VARCHAR} is null)              "
            + "    and (t.contract_no like CONCAT ('%',#{p1.contract_no,jdbcType=VARCHAR},'%') or #{p1.contract_no,jdbcType=VARCHAR} is null)     "
            + "      ")
    IPage<BScheduleOrderVo> selectPage(Page page, @Param("p1") BScheduleOrderVo searchCondition);

    /**
     * 按条件获取所有数据，没有分页
     * @param id
     * @return
     */
    @Select("                                        "
            + common_select
            + "  where t.id =  #{p1,jdbcType=INTEGER}")
    BScheduleOrderVo selectId(@Param("p1") int id);

    /**
     * 按订单编号，合同编号，来源获取数据
     */
    @Select("    "
            + "  select t.* from b_schedule_order t where true                                                                                          "
            + "         and t.order_no =  #{p1.order_no,jdbcType=VARCHAR}                                                                          "
            + "         and t.contract_no =  #{p1.contract_no,jdbcType=VARCHAR}                                                                    "
            + "      ")
    List<BScheduleOrderEntity> selectOrderByContract(@Param("p1") BScheduleOrderVo vo);
}
