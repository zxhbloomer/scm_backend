package com.xinyirun.scm.core.system.mapper.business.po.cargo_right_transfer;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xinyirun.scm.bean.entity.business.po.cargo_right_transfer.BPoCargoRightTransferAttachEntity;
import com.xinyirun.scm.bean.system.vo.business.po.cargo_right_transfer.BPoCargoRightTransferAttachVo;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

/**
 * <p>
 * 货权转移附件表 Mapper 接口
 * </p>
 *
 * @author xinyirun
 * @since 2025-07-20
 */
@Repository
public interface BPoCargoRightTransferAttachMapper extends BaseMapper<BPoCargoRightTransferAttachEntity> {

    @Select("select * from b_po_cargo_right_transfer_attach where cargo_right_transfer_id = #{p1}")
    BPoCargoRightTransferAttachVo selectByCargoRightTransferId(@Param("p1") Integer id);
}