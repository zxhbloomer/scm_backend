package com.xinyirun.scm.core.system.mapper.business.po.poorder;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xinyirun.scm.bean.entity.business.po.poorder.BPoOrderAttachEntity;
import com.xinyirun.scm.bean.system.vo.business.po.poorder.BPoOrderAttachVo;
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

    @Select("""
            -- 根据采购订单ID查询附件信息
            select * from b_po_order_attach 
            -- #{p1}: 采购合同id
            where po_order_id = #{p1}
            """)
    BPoOrderAttachVo selByPoOrderId(@Param("p1") Integer id);
}
