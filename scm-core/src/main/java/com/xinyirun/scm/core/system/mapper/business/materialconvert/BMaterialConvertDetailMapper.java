package com.xinyirun.scm.core.system.mapper.business.materialconvert;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xinyirun.scm.bean.entity.busniess.materialconvert.BMaterialConvertDetailEntity;
import com.xinyirun.scm.bean.system.vo.business.materialconvert.BMaterialConvertDetailVo;
import com.xinyirun.scm.bean.system.vo.business.materialconvert.BMaterialConvertVo;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>
 * 物料转换明细 Mapper 接口
 * </p>
 *
 * @author wwl
 * @since 2022-05-09
 */
@Repository
public interface BMaterialConvertDetailMapper extends BaseMapper<BMaterialConvertDetailEntity> {

    String common_select = "  "
            + "      SELECT                                                                                                                     "
            + "             t.*,                                                                                                      			"
            + "             concat('1:', cast(t.calc as char)) relation,                                                                        "
            + "             tt.idx,                                                                                                      		"
            + "             t4.code ,                                                                                                 			"
            + "             t4.name ,                                                                                                 			"
            + "             t4.owner_id ,                                                                                             		    "
            + "             t4.owner_code ,                                                                                                     "
            + "             ifnull(t5.short_name,t5.name) as owner_name,                                                                   		"
            + "             ifnull(t8.short_name,t8.name) as warehouse_name,                                                      			    "
            + "             t8.code as warehouse_code,                                                                                     	    "
            + "             t8.id as warehouse_id,                                                                                     		    "
            + "             t9.spec target_spec,                                                                                                 	"
            + "             t9.pm target_pm,                                                                                                   	"
            + "             t10.spec source_spec,                                                                                                 	"
            + "             t10.pm source_pm,                                                                                                   	"
            + "             t11.name as source_goods_name,                                                                                   		"
            + "             t12.name as target_goods_name,                                                                                   		"
            + "             t1.name as c_name,                                                                                        			"
            + "             t2.name as u_name                                                                                         			"
            + "        FROM                                                                                                           			"
            + "   	       b_material_convert_detail t                                                                                          "
            + "   LEFT JOIN m_staff t1 ON t.c_id = t1.id                                                                              			"
            + "   LEFT JOIN m_staff t2 ON t.u_id = t2.id                                                                              			"
            + "   LEFT JOIN b_material_convert t4 ON t.material_convert_id = t4.id                                                              "
            + "   LEFT JOIN m_owner t5 ON t4.owner_id = t5.id                                                                         		    "
            + "   LEFT JOIN m_warehouse t8 ON t4.warehouse_id = t8.id                                                             			    "
            + "   LEFT JOIN m_goods_spec t9 ON t.target_sku_id = t9.id                                                                     		"
            + "   LEFT JOIN m_goods_spec t10 ON t.source_sku_id = t10.id                                                                     	    "
            + "   LEFT JOIN m_goods t11 ON t10.goods_id = t11.id                                                                      			"
            + "   LEFT JOIN m_goods t12 ON t9.goods_id = t12.id                                                                      			"
            + "   INNER JOIN (                                                                                                                  "
            + "   			  select row_number() over(partition by t.material_convert_id                                                       "
            + "   			                               order by t.c_time asc) as idx,                                                       "
            + "   				t.id                                                                                                            "
            + "   			from b_material_convert_detail t                                                                                    "
            + "     ) tt on tt.id = t.id                                                                                                        "
            + "                                                                                                                       		    "
            ;

    /**
     * 页面查询列表
     * @param searchCondition
     * @return
     */
    @Select("   "
            + common_select
            + "   where true                                                                                                    "
            + "    and (t.material_convert_id =  #{p1.material_convert_id,jdbcType=INTEGER} )                                   "
            + "       ")
    List<BMaterialConvertDetailVo> selectList(@Param("p1") BMaterialConvertVo searchCondition);

    /**
     * 没有分页，按id筛选条件
     * @param searchCondition
     * @return
     */
    @Select("   <script>   "
            + "  select t.* from b_material_convert_detail t                           "
            + "  where t.id in "
            + "        <foreach collection='p1' item='item' index='index' open='(' separator=',' close=')'>    "
            + "         #{item.id,jdbcType=INTEGER}  "
            + "        </foreach>    "
            + "  </script>    ")
    List<BMaterialConvertDetailEntity> selectIdsIn(@Param("p1") List<BMaterialConvertDetailVo> searchCondition);

}
