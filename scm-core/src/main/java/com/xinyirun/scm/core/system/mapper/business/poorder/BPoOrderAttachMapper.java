package com.xinyirun.scm.core.system.mapper.business.poorder;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xinyirun.scm.bean.entity.busniess.poorder.BPoOrderAttachEntity;
import com.xinyirun.scm.bean.system.vo.business.poorder.PoOrderAttachVo;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

/**
 * <p>
 * 采购订单附件表 Mapper 接口
 * </p>
 *
 * @author xinyirun
 * @since 2025-01-14
 */
@Repository
public interface BPoOrderAttachMapper extends BaseMapper<BPoOrderAttachEntity> {

    @Select("select * from b_po_order_attach where po_order_id = #{p1}")
    PoOrderAttachVo selByPoOrderId(@Param("p1") Integer id);
}
