package com.xinyirun.scm.core.api.mapper.business.in.order;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xinyirun.scm.bean.entity.busniess.in.order.BInOrderEntity;
import com.xinyirun.scm.bean.entity.busniess.order.BOrderEntity;
import com.xinyirun.scm.bean.system.vo.wms.in.order.BInOrderVo;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

/**
 * <p>
 * 入库订单 Mapper 接口
 * </p>
 *
 * @author htt
 * @since 2021-10-29
 */
@Repository
public interface ApiInOrderMapper extends BaseMapper<BInOrderEntity> {

    /**
     * 按订单编号，合同编号获取数据
     * @param vo
     * @return
     */
    @Select("    "
            + "  select t.* from b_in_order t where true                                                                                          "
            + "         and (t.order_no =  #{p1.order_no,jdbcType=VARCHAR} or #{p1.order_no,jdbcType=VARCHAR} is null)                            "
            + "      ")
    BInOrderEntity selectOrderByContract(@Param("p1") BInOrderVo vo);

    /**
     * 按订单编号，合同编号获取数据
     * @param order_no
     * @return
     */
    @Select("    "
            + "  select t.* from b_order t where true                                                                                          "
            + "         and (t.order_no =  #{p1,jdbcType=VARCHAR})                            "
            + "      ")
    BOrderEntity selectOrderByOrderNo(@Param("p1") String order_no);

}
