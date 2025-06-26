package com.xinyirun.scm.core.system.mapper.business.soorder;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xinyirun.scm.bean.entity.busniess.soorder.BSoOrderAttachEntity;
import com.xinyirun.scm.bean.system.vo.business.soorder.SoOrderAttachVo;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

/**
 * <p>
 * 销售订单附件表 Mapper 接口
 * </p>
 *
 * @author xinyirun
 * @since 2025-01-14
 */
@Repository
public interface BSoOrderAttachMapper extends BaseMapper<BSoOrderAttachEntity> {

    @Select("select * from b_so_order_attach where so_order_id = #{p1}")
    SoOrderAttachVo selBySoOrderId(@Param("p1") Integer id);
}
