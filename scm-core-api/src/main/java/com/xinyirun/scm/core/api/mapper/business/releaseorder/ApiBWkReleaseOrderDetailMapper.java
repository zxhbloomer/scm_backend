package com.xinyirun.scm.core.api.mapper.business.releaseorder;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xinyirun.scm.bean.entity.busniess.releaseorder.BWkReleaseOrderDetailEntity;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author Wang Qianfeng
 * @date 2022/11/30 16:32
 */
@Repository
public interface ApiBWkReleaseOrderDetailMapper extends BaseMapper<BWkReleaseOrderDetailEntity> {

    @Select("select 1 from b_wk_release_order_detail for update nowait")
    List<Integer> lockB_wk_release_order_detail00();
    
}
