package com.xinyirun.scm.core.api.mapper.business.in;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xinyirun.scm.bean.entity.busniess.wms.in.BInEntity;
import com.xinyirun.scm.common.constant.DictConstant;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 入库单 Mapper 接口
 *
 */
@Repository
public interface ApiInMapper extends BaseMapper<BInEntity> {


    /**
     * 作废入库单
     * @param inEntities
     */
    @Update("<script>"
            + " UPDATE                                                                                                  "
            + " b_in set status = '"+ DictConstant.DICT_B_IN_STATUS_TWO + "', inventory_account_id = null            "
            + " WHERE id in                                                                                             "
            + " <foreach collection='p1' item='item' index='index' open='(' separator=',' close=')'>                    "
            + "      #{item.id}                                                                                         "
            + " </foreach>                                                                                              "
            + " AND status NOT IN ('"+ DictConstant.DICT_B_IN_STATUS_TWO +"', '"+ DictConstant.DICT_B_IN_STATUS_TWO +"',"
            + "   '"+ DictConstant.DICT_B_IN_STATUS_TWO +"')                                                         "
            + "</script>                                                                                                "
    )
    void updateStatus2Cancel(@Param("p1") List<BInEntity> inEntities);

    /**
     * 更新待办状态
     * @param inEntities
     */
    @Select("<script>"
            + "		UPDATE b_in t1                                                                                      "
            + "		INNER JOIN b_todo t2 ON t1.id = t2.serial_id AND t2.serial_type = 'b_in'                            "
            + "		SET t2.STATUS = '1'                                                                                 "
            + "		WHERE t1.id in                                                                                      "
            + "     <foreach collection='p1' item='item' index='index' open='(' separator=',' close=')'>                "
            + "      #{item.id}                                                                                         "
            + "     </foreach>                                                                                          "
            + "     AND t1.status NOT IN ('"+ DictConstant.DICT_B_IN_STATUS_TWO +"', '"+ DictConstant.DICT_B_IN_STATUS_TWO +"',"
            + "     '"+ DictConstant.DICT_B_IN_STATUS_TWO +"')                                                       "
            + "</script>                                                                                                "
            + "      ")
    void updateTodoData(@Param("p1") List<BInEntity> inEntities);

}
