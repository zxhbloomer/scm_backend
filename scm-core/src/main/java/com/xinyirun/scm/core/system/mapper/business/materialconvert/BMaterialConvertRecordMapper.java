package com.xinyirun.scm.core.system.mapper.business.materialconvert;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xinyirun.scm.bean.entity.business.materialconvert.BConvertRecordEntity;
import com.xinyirun.scm.bean.entity.business.materialconvert.BMaterialConvertEntity;
import com.xinyirun.scm.bean.system.vo.business.materialconvert.BConvertRecordVo;
import com.xinyirun.scm.common.constant.DictConstant;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

/**
 * @author Wang Qianfeng
 * @date 2022/11/23 16:20
 */
@Repository
public interface BMaterialConvertRecordMapper extends BaseMapper<BConvertRecordEntity> {

    @Select("SELECT                                                                                                    "
            +  "    t.id,                                                                                              "
            +  "  	t.c_time,                                                                                          "
            +  "  	t.convert_code,                                                                                    "
            +  "  	t.convert_name,                                                                                    "
            +  "  	t.data_version,                                                                                    "
            +  "  	t3.name owner_name,                                                                                "
            +  "  	t2.name warehouse_name,                                                                            "
            +  "  	t.is_effective,                                                                                    "
            +  "  	t1.label type_name,                                                                                "
            +  "  	t.target_sku_code,                                                                                 "
            +  "  	t.target_goods_name,                                                                               "
            +  "  	t.target_sku_name,                                                                                 "
            +  "  	t.source_sku_code,                                                                                 "
            +  "  	t.source_sku_name,                                                                                 "
            +  "  	t.source_goods_name,                                                                               "
            +  "  	t.calc,                                                                                            "
            +  "  	t.source_qty,                                                                                      "
            +  "  	t.target_qty                                                                                       "
            +  "  FROM                                                                                                 "
            +  "    b_convert_record t                                                                                 "
            +  "  LEFT JOIN s_dict_data t1 ON t1.`code` = '" + DictConstant.DICT_B_MATERIAL_CONVERT_TYPE +"' AND t1.dict_value = t.type"
            +  "  LEFT JOIN m_warehouse t2 on t.warehouse_id = t2.id                                                   "
            +  "  LEFT JOIN m_owner t3 on t.owner_id = t3.id                                                           "
            +  "  WHERE true                                                                                           "
            +  "     and (t.warehouse_id = #{p1.warehouse_id} or #{p1.warehouse_id} is null)                           "
            +  "     and (t.owner_id = #{p1.owner_id} or #{p1.owner_id} is null)                                       "
            +  "     and (t.convert_code = #{p1.convert_code} or #{p1.convert_code} is null or #{p1.convert_code} = '')                                       "
            +  "     and (t.is_effective = #{p1.is_effective} or #{p1.is_effective} is null)                           "
            +  "     and (t.type = #{p1.type} or #{p1.type} is null or #{p1.type} = '')                                "
            +  "     and (date_format(t.c_time, '%Y-%m-%d') >= #{p1.start_time} or #{p1.start_time} is null or #{p1.start_time} = '')"
            +  "     and (date_format(t.c_time, '%Y-%m-%d') <= #{p1.end_time} or #{p1.end_time} is null or #{p1.end_time} = '')"
            +  "     and (t.convert_name like concat('%', #{p1.convert_name}, '%') or t.convert_code like concat('%', #{p1.convert_name}, '%') or #{p1.convert_name} is null or #{p1.convert_name} = '')"
            +  "     and (t.target_goods_name like concat('%', #{p1.target_goods_name}, '%')                           "
            +  "         or t.target_sku_code like concat('%', #{p1.target_goods_name}, '%')                           "
            +  "         or t.target_sku_name like concat('%', #{p1.target_goods_name}, '%')                           "
            +  "         or #{p1.target_goods_name} is null or #{p1.target_goods_name} = '' )                          "
            +  "     and (t.source_sku_code like concat('%', #{p1.source_goods_name}, '%')                             "
            +  "         or t.source_sku_name like concat('%', #{p1.source_goods_name}, '%')                           "
            +  "         or t.source_goods_name like concat('%', #{p1.source_goods_name}, '%')                         "
            +  "         or #{p1.source_goods_name} is null or #{p1.source_goods_name} = '' )                          "
    )
    IPage<BConvertRecordVo> selectPageList(@Param("p1") BConvertRecordVo searchCondition, Page<BMaterialConvertEntity> pageCondition);
}
