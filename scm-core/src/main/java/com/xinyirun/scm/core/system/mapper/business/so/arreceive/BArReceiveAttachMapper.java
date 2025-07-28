package com.xinyirun.scm.core.system.mapper.business.so.arreceive;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xinyirun.scm.bean.entity.business.so.arreceive.BArReceiveAttachEntity;
import com.xinyirun.scm.bean.system.vo.business.so.arreceive.BArReceiveAttachVo;
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
public interface BArReceiveAttachMapper extends BaseMapper<BArReceiveAttachEntity> {

    @Select("SELECT * FROM b_ar_receive_attach WHERE ar_receive_id = #{id}")
    BArReceiveAttachVo selectByBArReceiveId(Integer id);
}