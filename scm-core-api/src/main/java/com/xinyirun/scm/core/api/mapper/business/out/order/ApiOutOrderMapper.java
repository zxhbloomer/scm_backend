package com.xinyirun.scm.core.api.mapper.business.out.order;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xinyirun.scm.bean.entity.busniess.order.BOrderEntity;
import com.xinyirun.scm.bean.entity.busniess.wms.out.BOutOrderEntity;
import com.xinyirun.scm.bean.system.vo.business.wms.out.BOutOrderVo;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

/**
 * <p>
 * 出库订单合同信息 Mapper 接口
 * </p>
 *
 * @author htt
 * @since 2021-10-18
 */
@Repository
public interface ApiOutOrderMapper extends BaseMapper<BOutOrderEntity> {

    /**
     * 按订单编号，合同编号，来源获取数据
     * @param vo
     * @return
     */
    @Select("    "
            + "  select t.* from b_out_order t where true                                                                                          "
            + "         and t.order_no =  #{p1.order_no,jdbcType=VARCHAR}                                                                          "
            + "      ")
    BOutOrderEntity selectOrderByContract(@Param("p1") BOutOrderVo vo);

    /**
     * 按订单编号，合同编号，来源获取数据
     * @param vo
     * @return
     */
    @Select("    "
            + "  select t.* from b_order t where true                                                                                          "
            + "         and t.order_no =  #{p1,jdbcType=VARCHAR}                                                                          "
            + "      ")
    BOrderEntity selectOrderByOrderNo(@Param("p1") String order_no);


}
