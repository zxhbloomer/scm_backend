package com.xinyirun.scm.core.system.mapper.business.so.arrefundreceive;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xinyirun.scm.bean.entity.business.so.arrefundreceive.BArReFundReceiveAttachEntity;
import com.xinyirun.scm.bean.system.vo.business.so.arrefundreceive.BArReFundReceiveAttachVo;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

/**
 * <p>
 * 收款单附件表 Mapper 接口
 * </p>
 *
 * @author xinyirun
 * @since 2025-02-26
 */
@Repository
public interface BArReFundReceiveAttachMapper extends BaseMapper<BArReFundReceiveAttachEntity> {

    @Select("SELECT * FROM b_ar_refund_receive_attach WHERE ar_refund_receive_id = #{id}")
    BArReFundReceiveAttachVo selectByBArId(Integer id);
}