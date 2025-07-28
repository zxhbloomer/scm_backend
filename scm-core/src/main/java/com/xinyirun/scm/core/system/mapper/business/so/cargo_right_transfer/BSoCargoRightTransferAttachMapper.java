package com.xinyirun.scm.core.system.mapper.business.so.cargo_right_transfer;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xinyirun.scm.bean.entity.business.so.cargo_right_transfer.BSoCargoRightTransferAttachEntity;
import com.xinyirun.scm.bean.system.vo.business.so.cargo_right_transfer.BSoCargoRightTransferAttachVo;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

/**
 * <p>
 * 销售货权转移附件表 Mapper 接口
 * </p>
 *
 * @author xinyirun
 * @since 2025-07-27
 */
@Repository
public interface BSoCargoRightTransferAttachMapper extends BaseMapper<BSoCargoRightTransferAttachEntity> {

    @Select("select * from b_so_cargo_right_transfer_attach where cargo_right_transfer_id = #{p1}")
    BSoCargoRightTransferAttachVo selectByCargoRightTransferId(@Param("p1") Integer id);
}