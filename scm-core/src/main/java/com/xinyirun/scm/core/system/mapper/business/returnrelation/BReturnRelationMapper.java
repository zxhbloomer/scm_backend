package com.xinyirun.scm.core.system.mapper.business.returnrelation;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xinyirun.scm.bean.entity.business.wms.in.BInEntity;
import com.xinyirun.scm.bean.entity.business.returnrelation.BReturnRelationEntity;
import com.xinyirun.scm.bean.system.vo.business.returnrelation.BReturnRelationExportVo;
import com.xinyirun.scm.bean.system.vo.business.returnrelation.BReturnRelationVo;
import com.xinyirun.scm.common.constant.DictConstant;
import com.xinyirun.scm.common.constant.SystemConstants;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>
 * 退货表 Mapper 接口
 * </p>
 *
 * @author xinyirun
 * @since 2024-07-26
 */
@Repository
public interface BReturnRelationMapper extends BaseMapper<BReturnRelationEntity> {

    /**
     * 查询退货单列表是否存在审核通过的退货单信息
     */
    @Select("SELECT * FROM b_return_relation where serial_id = #{p1} and serial_type = #{p2} and status = '"+ DictConstant.DICT_B_RETURN_RELATION_STATUS_TG +"'")
    BReturnRelationEntity selectBySerialIdAndSerialType(@Param("p1") Integer id, @Param("p2") String dictBReturnRelation);

    /**
     * 查询退货单列表是否存在审核通过的退货单信息
     */
    @Select("  SELECT t1.*, t2.NAME AS unit_name FROM                                                                           "
            +"  b_return_relation t1                                                                                            "
            +"  LEFT JOIN m_unit t2 on t1.unit_id = t2.id                                                                       "
            +"  WHERE                                                                                                           "
            +"  serial_id = #{p1} and serial_type = #{p2} and status = '"+ DictConstant.DICT_B_RETURN_RELATION_STATUS_TG +"'    ")
    BReturnRelationVo selectBySerialIdAndSerialTypeVO(@Param("p1") Integer id, @Param("p2") String dictBReturnRelation);

    @Select( "  SELECT                                                                                    "
            +"  t1.*                                                                                      "
            +"  FROM                                                                                      "
            +"         b_return_relation t1                                                               "
            +"  LEFT JOIN b_monitor t2 ON t2.id = t1.serial_id                                            "
            +"  AND t1.serial_type = '"+ SystemConstants.SERIAL_TYPE.B_MONITOR+"'                         "
            +"  AND t1.STATUS = '"+ DictConstant.DICT_B_RETURN_RELATION_STATUS_TG +"'                     "
            +"  LEFT JOIN b_monitor_out t3 ON t3.monitor_id = t2.id                                       "
            +"  WHERE TRUE                                                                                "
            +"  AND t3.out_id = #{p1}                                                                     "
    )
    BReturnRelationEntity selectByOutIdAndSerialType(@Param("p1") Integer id);



    @Select(" SELECT                                                                                                                               "
            +" t1.*,                                                                                                                               "
            +" t2.label as status_name,                                                                                                            "
            +" t3.name as c_name,                                                                                                                  "
            +" t4.name as u_name,                                                                                                                  "
            +" t5.NAME AS unit_name,                                                                                                               "
            +" t7.code AS out_code                                                                                                             "
            +" FROM                                                                                                                                "
            +" b_return_relation t1                                                                                                                "
            +" LEFT JOIN s_dict_data t2 ON t1.STATUS = t2.dict_value  AND t2.CODE = '"+ DictConstant.DICT_B_RETURN_RELATION_STATUS +"'             "
            +" LEFT JOIN m_staff t3 ON t3.id = t1.c_id                                                                                             "
            +" LEFT JOIN m_staff t4 ON t4.id = t1.u_id                                                                                             "
            +" LEFT JOIN m_unit t5 on t1.unit_id = t5.id                                                                                           "
            +" LEFT JOIN b_monitor_out t6 ON t6.monitor_id = t1.serial_id and t1.serial_type = '"+ SystemConstants.SERIAL_TYPE.B_MONITOR+"'        "
            +" LEFT JOIN b_out t7 ON t7.id = t6.out_id                                                                                             "
            +" WHERE TRUE                                                                                                                          "
            +"  AND (t1.STATUS = #{p1.status} or #{p1.status} is null or #{p1.status} = '')                                                        "
            +"  AND (t1.code LIKE CONCAT('%', #{p1.code}, '%') or #{p1.code} is null or #{p1.code} ='' )                                           "
            +"  AND (t1.serial_code LIKE CONCAT('%', #{p1.serial_code}, '%') or #{p1.serial_code} is null or #{p1.serial_code} ='' )               "
    )
    IPage<BReturnRelationVo> selectPageList(Page<BInEntity> pageCondition,@Param("p1") BReturnRelationVo searchCondition);


    /**
     * 查询退货单列表是否存在审核通过的退货单信息
     */
    @Select("  SELECT t1.*, t2.NAME AS unit_name FROM                                                                           "
            +"  b_return_relation t1                                                                                            "
            +"  LEFT JOIN m_unit t2 on t1.unit_id = t2.id                                                                       "
            +"  WHERE                                                                                                           "
            +"  t1.id = #{p1}                                                                                                   ")
    BReturnRelationVo getDetail(@Param("p1") Integer id);

    @Select(" <script>                                                                                                                               "
            +" SELECT                                                                                                                               "
            +" t1.*,                                                                                                                               "
            +" t2.label as status_name,                                                                                                            "
            +" t3.name as c_name,                                                                                                                  "
            +" t4.name as u_name,                                                                                                                  "
            +" t5.NAME AS unit_name,                                                                                                                "
            +" @row_num:= @row_num+ 1 as excel_no                                                                                                  "
            +" FROM                                                                                                                                "
            +" b_return_relation t1                                                                                                                "
            +" LEFT JOIN s_dict_data t2 ON t1.STATUS = t2.dict_value  AND t2.CODE = '"+ DictConstant.DICT_B_RETURN_RELATION_STATUS +"'             "
            +" LEFT JOIN m_staff t3 ON t3.id = t1.c_id                                                                                             "
            +" LEFT JOIN m_staff t4 ON t4.id = t1.u_id                                                                                             "
            +" LEFT JOIN m_unit t5 on t1.unit_id = t5.id                                                                                           "
            +" ,(select @row_num:=0) t6                                                                                                           "
            +" WHERE TRUE                                                                                                                          "
            + "   <if test='p1!= null'>                                                                                                            "
            + "    and t1.id in                                                                                                                    "
            + "        <foreach collection='p1' item='item' index='index' open='(' separator=',' close=')'>                                        "
            + "         #{item.id}                                                                                                                 "
            + "        </foreach>                                                                                                                  "
            + "   </if>                                                                                                                            "
            + "  </script>                                                                                                                         "
    )
    List<BReturnRelationExportVo> selectExportList(@Param("p1") List<BReturnRelationVo> searchCondition);

    @Select(" SELECT                                                                                                                               "
            +" count(1) as count                                                                                                                "
            +" FROM                                                                                                                                "
            +" b_return_relation t1                                                                                                                "
            +" LEFT JOIN s_dict_data t2 ON t1.STATUS = t2.dict_value  AND t2.CODE = '"+ DictConstant.DICT_B_RETURN_RELATION_STATUS +"'             "
            +" LEFT JOIN m_staff t3 ON t3.id = t1.c_id                                                                                             "
            +" LEFT JOIN m_staff t4 ON t4.id = t1.u_id                                                                                             "
            +" LEFT JOIN m_unit t5 on t1.unit_id = t5.id                                                                                           "
            +" WHERE TRUE                                                                                                                          "
            +"  AND (t1.STATUS = #{p1.status} or #{p1.status} is null or #{p1.status} = '')                                                        "
            +"  AND (t1.code LIKE CONCAT('%', #{p1.code}, '%') or #{p1.code} is null or #{p1.code} ='' )                                           "
            +"  AND (t1.serial_code LIKE CONCAT('%', #{p1.serial_code}, '%') or #{p1.serial_code} is null or #{p1.serial_code} ='' )               "
    )
    Long selectPageMyCount(@Param("p1") BReturnRelationVo searchCondition);


    @Select(" SELECT                                                                                                                               "
            +" t1.*,                                                                                                                               "
            +" t2.label as status_name,                                                                                                            "
            +" t3.name as c_name,                                                                                                                  "
            +" t4.name as u_name,                                                                                                                  "
            +" t5.NAME AS unit_name,                                                                                                               "
            +" @row_num:= @row_num+ 1 as excel_no                                                                                                  "
            +" FROM                                                                                                                                "
            +" b_return_relation t1                                                                                                                "
            +" LEFT JOIN s_dict_data t2 ON t1.STATUS = t2.dict_value  AND t2.CODE = '"+ DictConstant.DICT_B_RETURN_RELATION_STATUS +"'             "
            +" LEFT JOIN m_staff t3 ON t3.id = t1.c_id                                                                                             "
            +" LEFT JOIN m_staff t4 ON t4.id = t1.u_id                                                                                             "
            +" LEFT JOIN m_unit t5 on t1.unit_id = t5.id                                                                                           "
            +" ,(select @row_num:=0) t6                                                                                                           "
            +" WHERE TRUE                                                                                                                          "
            +"  AND (t1.STATUS = #{p1.status} or #{p1.status} is null or #{p1.status} = '')                                                        "
            +"  AND (t1.code LIKE CONCAT('%', #{p1.code}, '%') or #{p1.code} is null or #{p1.code} ='' )                                           "
            +"  AND (t1.serial_code LIKE CONCAT('%', #{p1.serial_code}, '%') or #{p1.serial_code} is null or #{p1.serial_code} ='' )               "
    )
    List<BReturnRelationExportVo> selectExportAll(@Param("p1") BReturnRelationVo searchCondition);
}
