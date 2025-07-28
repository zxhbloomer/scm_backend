package com.xinyirun.scm.core.system.mapper.business.warehouse.relation;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xinyirun.scm.bean.entity.business.wms.warehouse.position.BWarehousePositionEntity;
import com.xinyirun.scm.bean.system.vo.business.wms.warehouse.position.BWarehousePositionVo;
import com.xinyirun.scm.bean.system.vo.business.wms.warehouse.position.MWarehouseTransferVo;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author xinyirun
 * @since 2022-02-27
 */
@Repository
public interface BWarehousePositionMapper extends BaseMapper<BWarehousePositionEntity> {


    @Delete("                                                                               "
            + "     delete from b_warehouse_position t                                      "
            + "      where t.serial_id = #{p1}                                              "
            + "        and t.serial_type = #{p2}                                            "
    )
    int deleteBySerialId(@Param("p1") Integer id , @Param("p2") String type );

    /**
     * 根据条件查询数量
     */
    @Select("                                                                                                           "
            + "     select count(1) c from b_warehouse_position t                                                       "
            + "      where true                                                                                         "
            + "        and t.warehouse_code = #{p1.warehouse_code}                                    "
            + "     group by t.warehouse_code                                                                  "
    )
    Integer selectCountByWarehouseCode(@Param("p1") BWarehousePositionVo searchCondition );


    /**
     * 按条件获取所有数据，没有分页
     * @param searchCondition
     * @return
     */
    /**
     * 按条件获取所有数据，没有分页
     * @param searchCondition
     * @return
     */
    @Delete("                                                                                        "
            + "   delete from b_warehouse_position t4                                                "
            + "     	  where exists (                                                             "
            + "                select 1                                                              "
            + "                  from (                                                              "
            + "              	  	  	select t2.id                                                 "
            + "   	  	   			  from m_position t1                                             "
            + "   	            inner join b_warehouse_position t2                                   "
            + "   	                    on t2.serial_id = t1.id                                      "
            + "   	            inner join m_warehouse t3                                            "
            + "   	                    on t2.warehouse_id = t3.id                                   "
            + "   	  	             where (t1.id = #{p1.position_id,jdbcType=INTEGER}   )           "
            + "   	                   and t4.id = t2.id                                             "
            + "                          ) sub                                                       "
            + "     	  )                                                                          "
            + "      ")
    void realDeleteByCode(@Param("p1") MWarehouseTransferVo searchCondition);

    /**
     * 获取全部仓库
     * @param condition
     * @return
     */
    @Select("                                                                        "
            + "      select t1.id AS `key`,                                          "
            + "             t1.enable,                                               "
            + "             t1.short_name AS label                                   "
            + "        from m_warehouse t1                                           "
            + "       where true                                                     "
            + "    order by t1.name                                                  "
            + "                                                                          ")
    List<MWarehouseTransferVo> getAllWarehouseTransferList(@Param("p1") MWarehouseTransferVo condition);

    /**
     * 获取该岗位下，全部仓库
     * @param condition
     * @return
     */
    @Select("                                                                                   "
            + "       	 select t3.id                                                           "
            + "       	   from b_warehouse_position t1                                         "
            + "        inner join m_position t2                                                 "
            + "                on t1.serial_id = t2.id                                          "
            + "        inner join m_warehouse t3                                                "
            + "                on t1.warehouse_id = t3.id                                       "
            + "       	    where (t2.code = #{p1.code,jdbcType=VARCHAR})                       "
            + "                                                            ")
    List<Long> getUsedWarehouseTransferList(@Param("p1")MWarehouseTransferVo condition);
}
