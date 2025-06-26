package com.xinyirun.scm.core.system.mapper.business.allocate;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xinyirun.scm.bean.entity.busniess.allocate.BAllocateOrderEntity;
import com.xinyirun.scm.bean.system.vo.business.allocate.BAllocateOrderVo;
import com.xinyirun.scm.common.constant.DictConstant;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>
 * 入库订单 Mapper 接口
 * </p>
 *
 * @author wwl
 * @since 2022-02-09
 */
@Repository
public interface BAllocateOrderMapper extends BaseMapper<BAllocateOrderEntity> {

    /**
     * 按订单编号，合同编号获取数据
     * @param vo
     * @return
     */
    @Select("    "
            + "  select t.* from b_allocate_order t where true                                                                                    "
            + "         and (t.order_no =  #{p1.order_no,jdbcType=VARCHAR} or #{p1.order_no,jdbcType=VARCHAR} is null)                            "
            + "         and (t.contract_no =  #{p1.contract_no,jdbcType=VARCHAR} or #{p1.contract_no,jdbcType=VARCHAR} is null)                   "
            + "      ")
    List<BAllocateOrderEntity> selectOrderByContract(@Param("p1") BAllocateOrderVo vo);

    String common_select = "  "
            + "      SELECT                                                                                                                       "
            + "             t.*,                                                                                                                  "
            + "             ifnull(t3.short_name,t3.name) as supplier_name,                                                                       "
            + "             t5.label as bill_type_name ,                                                                                          "
            + "             t1.name as c_name,                                                                                                    "
            + "             t2.name as u_name                                                                                                     "
            + "        FROM                                                                                                                       "
            + "   	       b_allocate_order t                                                                                                     "
            + "   LEFT JOIN m_staff t1 ON t.c_id = t1.id                                                                                          "
            + "   LEFT JOIN m_staff t2 ON t.u_id = t2.id                                                                                          "
            + "   LEFT JOIN m_customer t3 ON t.supplier_id = t3.id                                                                                "
            + "   LEFT JOIN (select tab1.* from s_dict_data tab1 inner join s_dict_type tab2 on tab1.dict_type_id = tab2.id                       "
            + "              where tab2.code = '" + DictConstant.DICT_B_ALLOCATE_BUSINESS_TYPE + "')t5 on t5.dict_value = t.bill_type             "
            + "                                                                        "
            ;

    /**
     * 页面查询列表
     * @param page
     * @param searchCondition
     * @return
     */
    @Select("    "
            + common_select
            + "  where true                                                                                                                   "
            + "    and (t.order_no like CONCAT ('%',#{p1.order_no,jdbcType=VARCHAR},'%') or #{p1.order_no,jdbcType=VARCHAR} is null)          "
            + "    and (t.contract_no like CONCAT ('%',#{p1.contract_no,jdbcType=VARCHAR},'%') or #{p1.contract_no,jdbcType=VARCHAR} is null) "
            + "      ")
    IPage<BAllocateOrderVo> selectPage(Page page, @Param("p1") BAllocateOrderVo searchCondition);
    
    /**
     * 按条件获取所有数据，没有分页
     * @param id
     * @return
     */
    @Select("    "
            + common_select
            + "  where t.id =  #{p1,jdbcType=INTEGER}"
            + "      ")
    BAllocateOrderVo selectId(@Param("p1") int id);

    /**
     * 按订单号查询数据
     */
    @Select("    "
            + common_select
            + "  where t.order_no =  #{p1,jdbcType=VARCHAR}"
            + "      ")
    BAllocateOrderEntity selectByOrderNo(@Param("p1") String orderNo);
}
