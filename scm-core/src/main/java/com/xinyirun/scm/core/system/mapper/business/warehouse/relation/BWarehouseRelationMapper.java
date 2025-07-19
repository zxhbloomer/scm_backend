package com.xinyirun.scm.core.system.mapper.business.warehouse.relation;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xinyirun.scm.bean.entity.busniess.wms.warehouse.relation.BWarehouseRelationEntity;
import com.xinyirun.scm.bean.system.vo.business.wms.warehouse.relation.BWarehouseRelationVo;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author xinyirun
 * @since 2022-02-27
 */
@Repository
public interface BWarehouseRelationMapper extends BaseMapper<BWarehouseRelationEntity> {


    @Delete("                                                                                                           "
            + "     delete from b_warehouse_relation t                                                                  "
            + "      where t.serial_id = #{p1}                                                                          "
            + "        and t.serial_type = #{p2}                                                                        "
    )
    int deleteBySerialId(@Param("p1") Integer id , @Param("p2") String type );

    /**
     * 根据条件查询数量
     */
    @Select("                                                                                                           "
            + "     select count(1) c from b_warehouse_relation t                                                       "
            + "      where true                                                                                         "
            + "        and t.warehouse_relation_code = #{p1.warehouse_relation_code}                                    "
            + "     group by t.warehouse_relation_code                                                                  "
    )
    Integer selectCountByRelationCode(@Param("p1") BWarehouseRelationVo searchCondition );

    @Delete("                                                                                                           "
            + "     delete from b_warehouse_relation t                                                                  "
            + "      where t.staff_id = #{p1}                                                                           "
    )
    int deleteByStaffId(@Param("p1") Integer staffId);

    @Delete("                                                                                                           "
            + "     delete from b_warehouse_relation t                                                                  "
            + "      where t.position_id = #{p1}                                                                           "
    )
    int deleteByPositionId(@Param("p1") Integer positionId);
}

