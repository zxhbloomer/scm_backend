package com.xinyirun.scm.core.api.mapper.master.warehouse;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xinyirun.scm.bean.entity.master.warehouse.MBinEntity;
import com.xinyirun.scm.common.constant.SystemConstants;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

/**
 * <p>
 * 库位 Mapper 接口
 * </p>
 *
 * @author htt
 * @since 2021-09-23
 */
@Repository
public interface ApiBinMapper extends BaseMapper<MBinEntity> {

    String common_select = "  "
            + "     SELECT                                                             "
            + "            t.*,                                                        "
            + "            t4.name as location_name,                                          "
            + "            t4.id as location_id,                                          "
            + "            t3.name as warehouse_name,                                          "
            + "            t3.id as warehouse_id,                                          "
            + "            t1.name as c_name,                                          "
            + "            t2.name as u_name                                           "
            + "       FROM                                                             "
            + "  	       m_bin t                                                  "
            + "  LEFT JOIN m_staff t1 ON t.c_id = t1.id                                 "
            + "  LEFT JOIN m_staff t2 ON t.u_id = t2.id                                 "
            + "  LEFT JOIN m_warehouse t3 ON t.warehouse_id = t3.id                                 "
            + "  LEFT JOIN m_location t4 ON t.location_id = t4.id                                 "
            + "                                                                        "
            ;

    /**
     * 按条件获取所有数据，没有分页
     * @param warehouse_id,location_id
     * @return
     */
    @Select("    "
            + "  SELECT                                                                                                                                                   "
            + "         t3.*                                                                                                                                  "
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
    MBinEntity selectByWarehouseId(@Param("p1")int warehouse_id);

}
