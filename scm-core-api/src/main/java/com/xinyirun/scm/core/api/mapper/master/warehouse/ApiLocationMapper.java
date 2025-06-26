package com.xinyirun.scm.core.api.mapper.master.warehouse;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xinyirun.scm.bean.entity.master.warehouse.MLocationEntity;
import com.xinyirun.scm.common.constant.SystemConstants;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author htt
 * @since 2021-09-23
 */
@Repository
public interface ApiLocationMapper extends BaseMapper<MLocationEntity> {


    /**
     * 按条件获取所有数据，没有分页
     * @param warehouse_id
     * @return
     */
    @Select("    "
            + "  SELECT                                                                                                                                                   "
            + "         t2.*                                                                                                                                  "
            + "    FROM                                                                                                                                       "
            + "         m_warehouse t1                                                                                                                        "
            + "         LEFT JOIN m_location t2 ON t1.id = t2.warehouse_id AND t2.enable = '"+ SystemConstants.ENABLE_TRUE + "'                             "
            + "         LEFT JOIN m_bin t3 ON t3.warehouse_id = t1.id  AND t3.location_id = t2.id AND t3.enable = '"+ SystemConstants.ENABLE_TRUE + "'      "
            + "    WHERE                                                                                                                                      "
            + "         t1.id =  #{p1,jdbcType=INTEGER}                                                                                                      "
            + "         and t1.enable_location = '"+ SystemConstants.ENABLE_FALSE +"'                                                                       "
            + "         and t1.enable_bin = '"+ SystemConstants.ENABLE_FALSE +"'                                                                            "
            + "         and t1.enable = '"+ SystemConstants.ENABLE_TRUE +"'                                                                                 "
            + "      ")
    MLocationEntity selectByWarehouseId(@Param("p1")int warehouse_id);

}
