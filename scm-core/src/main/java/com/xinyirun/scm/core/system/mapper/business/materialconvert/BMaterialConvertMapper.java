package com.xinyirun.scm.core.system.mapper.business.materialconvert;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xinyirun.scm.bean.entity.business.materialconvert.BMaterialConvertEntity;
import com.xinyirun.scm.bean.system.vo.business.materialconvert.*;
import com.xinyirun.scm.bean.system.vo.excel.materialconvert.BMaterialConvertExportVo;
import com.xinyirun.scm.common.constant.DictConstant;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>
 * 物料转换 Mapper 接口
 * </p>
 *
 * @author wwl
 * @since 2022-05-09
 */
@Repository
public interface BMaterialConvertMapper extends BaseMapper<BMaterialConvertEntity> {

    String common_select = "  "
            + "      SELECT                                                                                                                     "
            + "             t4.is_effective ,                                                                                                 	"
            + "             t.*,                                                                                                      			"
            + "             concat(t4.name,'v',t4.data_version) convert_name,                                                                   "
            + "             t16.label status_name  ,                                                                                    		"
            + "             tt.idx,                                                                                                      		"
            + "             t15.count,                                                                                                      	"
            + "             t4.code ,                                                                                                 			"
            + "             t4.type ,                                                                                                 			"
            + "             t4.name ,                                                                                                 			"
            + "             t15.label type_name  ,                                                                                    			"
            + "             t4.sku_id ,                                                                                                 		"
            + "             t4.sku_code ,                                                                                                 		"
            + "             t4.is_sku ,                                                                                                     	"
            + "             t4.owner_id ,                                                                                             		    "
            + "             t4.owner_code ,                                                                                                     "
            + "             ifnull(t5.short_name,t5.name) as owner_name,                                                                   	    "
            + "             ifnull(t8.short_name,t8.name) as warehouse_name,                                                      			    "
            + "             t8.code as warehouse_code,                                                                                     	    "
            + "             t8.id as warehouse_id,                                                                                     		    "
            + "             t9.spec target_spec,                                                                                                "
            + "             t9.pm target_pm,                                                                                                    "
            + "             t10.spec source_spec,                                                                                               "
            + "             t10.pm source_pm,                                                                                                   "
            + "             t11.name as source_goods_name,                                                                                   	"
            + "             t12.name as target_goods_name,                                                                                   	"
            + "             t13.spec spec,                                                                                                      "
            + "             t13.pm pm,                                                                                                          "
            + "             t14.name as goods_name,                                                                                   	        "
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
            + "   LEFT JOIN m_goods_spec t10 ON t.source_sku_id = t10.id                                                                     	"
            + "   LEFT JOIN m_goods t11 ON t10.goods_id = t11.id                                                                      			"
            + "   LEFT JOIN m_goods t12 ON t9.goods_id = t12.id                                                                      			"
            + "   LEFT JOIN m_goods_spec t13 ON t4.sku_id = t13.id                                                                       		"
            + "   LEFT JOIN m_goods t14 ON t13.goods_id = t14.id                                                                      			"
            + "   LEFT JOIN v_dict_info t15 on t15.code = '" + DictConstant.DICT_B_MATERIAL_CONVERT_TYPE + "' and t15.dict_value = t4.type      "
            + "   LEFT JOIN v_dict_info t16 on t16.code = '" + DictConstant.DICT_B_MATERIAL_CONVERT_STATUS + "' and t16.dict_value = t.status   "

            + "	  LEFT JOIN(  SELECT                                                                                                            "
            + "	    	t.material_convert_id,                                                                                                  "
            + "	    	count(1) count                                                                                                          "
            + "	    FROM                                                                                                                        "
            + "	    	b_material_convert_detail t                                                                                             "
            + "	    	WHERE t.is_effective = true                                                                                             "
            + "	    GROUP BY                                                                                                                    "
            + "	    	t.material_convert_id) t15 ON t15.material_convert_id = t.material_convert_id                                           "

            + "   INNER JOIN (                                                                                                                  "
            + "   			  select row_number() over(partition by t.material_convert_id                                                       "
            + "   			                               order by t.c_time asc) as idx,                                                       "
            + "   				t.id                                                                                                            "
            + "   			from b_material_convert_detail t                                                                                    "
            + "     ) tt on tt.id = t.id                                                                                                        "
            + "                                                                                                                       		    "
            ;

    String export_sql = ""
            + "  SELECT                                                                                                "
            +    "      t1.`code`,                                                                                     "
            +    "  	ifnull(t2.short_name , t2.`name`) owner_name,                                                  "
            +    "  	t4.label type_name,                                                                            "
            +    "      if(t1.is_effective, '已启用','未启用' ) effective_name,                                          "
            +    "  	t12.`name` source_goods_name,                                                                  "
            +    "  	t11.`code` source_sku_code,                                                                    "
            +    "  	t11.`spec` source_spec,                                                                        "
            +    "  	t6.`name` target_goods_name,                                                                   "
            +    "  	t5.`code` target_sku_code,                                                                     "
            +    "      t5.spec target_spec,                                                                           "
            +    "  	t1.convert_time,                                                                               "
            +    "      t8.name c_name,                                                                                "
            +    "      t9.name u_name,                                                                                 "
            +    "      @row_num:= @row_num+ 1 as excel_no,                                                                  "
            +    "      t1.c_time,                                                                                     "
            +    "      t1.u_time                                                                                      "
            +    "  FROM                                                                                               "
            +    "    b_material_convert t1                                                                            "
            +    "  LEFT JOIN m_owner t2 ON t1.owner_id = t2.id                                                        "
            +    "  LEFT JOIN v_dict_info t4 on t4.code ='" + DictConstant.DICT_B_MATERIAL_CONVERT_TYPE + "' and t4.dict_value = t1.type"
            +    "  LEFT JOIN m_goods_spec t5 ON t5.id = t1.sku_id                                                     "
            +    "  LEFT JOIN m_goods t6 ON t6.id = t5.goods_id                                                        "
            +    "  LEFT JOIN m_staff t8 ON t1.c_id =  t8.id                                                            "
            +    "  LEFT JOIN m_staff t9 ON t1.u_id = t9.id                                                            "
            +    "  LEFT JOIN b_material_convert_detail t10 ON t1.id = t10.material_convert_id                         "
            +    "  LEFT JOIN m_goods_spec t11 ON t11.id = t10.source_sku_id                                           "
            +    "  LEFT JOIN m_goods t12 ON t12.id = t11.goods_id                                                     "
            + "   ,(select @row_num:=0) t3                                                                             ";


    String selectConvertPricePage = "	SELECT                                                                                                                                                                                              "
            +	"		t1.code,                                                                                       "
            +	"		t1.amount,                                                                                     "
            +	"		t1.qty,                                                                                        "
            +	"		ifnull(t1.amount/t1.qty, 0) price,                                                             "
            +	"		t2.id owner_id,                                                                                "
            +	"		t2.NAME owner_name,                                                                            "
            +	"		t2.CODE owner_code,                                                                            "
            +	"		t2.short_name owner_simple_name,                                                               "
            +	"		t3.id warehouse_id,                                                                            "
            +	"		t3.NAME warehouse_name,                                                                        "
            +	"		t3.short_name warehouse_simple_name,                                                           "
            +	"		t4.id location_id,                                                                             "
            +	"		t4.NAME location_name,                                                                         "
            +	"		t4.short_name location_simple_name,                                                            "
            +	"		t5.id bin_id,                                                                                  "
            +	"		t5.NAME bin_name,                                                                              "
            +	"		t5.NAME bin_simple_name,                                                                       "
            +	"		t6.id sku_id,                                                                                  "
            +	"		t6.CODE sku_code,                                                                              "
            +	"		t6.NAME sku_name,                                                                              "
            +   "       t6.spec,                                                                                       "
            +	"		t7.id goods_id,                                                                                "
            +	"		t7.CODE goods_code,                                                                            "
            +	"		t7.NAME goods_name,                                                                            "
            +	"		t8.id category_id,                                                                             "
            +	"		t8.CODE category_code,                                                                         "
            +	"		t8.NAME category_name,                                                                         "
            +	"		t9.id industry_id,                                                                             "
            +	"		t9.CODE industry_code,                                                                         "
            +	"		t9.NAME industry_name,                                                                         "
            +	"		t10.id business_id,                                                                            "
            +	"		t10.CODE business_code,                                                                        "
            +	"		t10.NAME business_name,                                                                        "
            +	"		t11.CODE prop_id,                                                                              "
            +	"		t11.NAME prop_name,                                                                            "
            +   "       concat(t1.warehouse_id, '_', t1.owner_id, '_', t1.sku_id) id,                                  "
            +   "       DATE_FORMAT(t1.dt,'%Y-%m-%d') as dt                                                            "
            +	"	FROM                                                                                               "
            +	"		(                                                                                              "
            +	"		SELECT                                                                                         "
            +	"			tt1.warehouse_id,                                                                          "
            +	"			tt1.location_id,                                                                           "
            +	"			tt1.bin_id,                                                                                "
            +	"			tt1.owner_id,                                                                              "
            +	"			tt2.target_sku_id sku_id,                                                                  "
            +	"			tt2.source_sku_id,                                                                         "
            +	"			tt1.dt,                                                                                    "
            +	"			tt2.CODE,                                                                                  "
            +	"			sum( ifnull( tt1.qty_in, 0 ) ) over ( PARTITION BY tt1.sku_id, tt1.warehouse_id, tt1.owner_id ORDER BY tt1.dt ROWS BETWEEN 14 PRECEDING AND 0 FOLLOWING ) qty,"
            +	"			sum( tt1.qty_in * ifnull( tt1.price, 0 ) ) over ( PARTITION BY tt1.sku_id, tt1.warehouse_id, tt1.owner_id ORDER BY tt1.dt ROWS BETWEEN 14 PRECEDING AND 0 FOLLOWING ) amount"
            +	"		FROM                                                                                           "
            +	"			b_daily_inventory tt1                                                                      "
            +	"			INNER JOIN (                                                                               "
            +	"			SELECT                                                                                     "
            +	"				t1.CODE,                                                                               "
            +	"				t2.target_sku_id,                                                                      "
            +	"				t2.source_sku_id,                                                                      "
            +	"				t1.warehouse_id,                                                                       "
            +	"				t1.owner_id                                                                            "
            +	"			FROM                                                                                       "
            +	"				b_material_convert t1                                                                  "
            +	"				INNER JOIN b_material_convert_detail t2 ON t2.material_convert_id = t1.id              "
            +	"			) tt2 ON tt2.source_sku_id = tt1.sku_id                                                    "
            +	"			AND tt2.warehouse_id = tt1.warehouse_id                                                    "
            +	"			AND tt2.owner_id = tt1.owner_id                                                            "
            +	"		) t1                                                                                           "
            +	"		LEFT JOIN m_owner t2 ON t1.owner_id = t2.id                                                    "
            +	"		LEFT JOIN m_warehouse t3 ON t1.warehouse_id = t3.id                                            "
            +	"		LEFT JOIN m_location t4 ON t1.location_id = t4.id                                              "
            +	"		LEFT JOIN m_bin t5 ON t1.bin_id = t5.id                                                        "
            +	"		LEFT JOIN m_goods_spec t6 ON t6.id = t1.sku_id                                                 "
            +	"		LEFT JOIN m_goods t7 ON t7.id = t6.goods_id                                                    "
            +	"		LEFT JOIN m_category t8 ON t8.id = t7.category_id                                              "
            +	"		LEFT JOIN m_industry t9 ON t9.id = t8.industry_id                                              "
            +	"		LEFT JOIN m_business_type t10 ON t10.id = t9.business_id                                       "
            +	"		LEFT JOIN m_goods_spec_prop t11 ON t11.id = t6.prop_id                                         "
            +	"	WHERE                                                                                              "
            +	"		DATE_FORMAT(t1.dt, '%Y-%m-%d')  = DATE_FORMAT( DATE_SUB(CURDATE(), INTERVAL 1 DAY), '%Y-%m-%d')"
            +   "       and (t7.NAME like concat('%', #{p1.goods_name}, '%') or #{p1.goods_name} is null or #{p1.goods_name} = '') "
            +   "       and (t3.id = #{p1.warehouse_id} or #{p1.warehouse_id} is null)                                 "
            +   "       and (t10.NAME like concat('%', #{p1.business_name}, '%') or #{p1.business_name} is null or #{p1.business_name} = '') "
            +	"	GROUP BY                                                                                           "
            +	"		t1.warehouse_id,                                                                               "
            +	"		t1.owner_id,                                                                                   "
            +	"		t1.sku_id                                                                                      ";

    /**
     * 页面查询列表
     * @param page
     * @param searchCondition
     * @return
     */
    @Select("   "
            + common_select
            + "   where true                                                                                                                                    "
            + "  and (t4.is_effective =  #{p1.is_effective,jdbcType=BOOLEAN} or #{p1.is_effective,jdbcType=BOOLEAN} is null  )                                  "
            + "  and (t4.code like concat('%', #{p1.code}, '%') or #{p1.code} is null or #{p1.code} = '')                                                       "
            + "  and (t4.owner_id = #{p1.owner_id} or #{p1.owner_id} is null)                                                                                   "
            + "  and (t5.id = #{p1.owner_id} or #{p1.owner_id} is null)                                                                                         "
            + "  and (t4.warehouse_id = #{p1.warehouse_id} or #{p1.warehouse_id} is null)                                                                       "
            + "  and (t12.name LIKE CONCAt('%', #{p1.target_goods_name}, '%') or t10.spec LIKE CONCAt('%', #{p1.target_goods_name}, '%')                        "
            + "  or t.target_sku_code LIKE CONCAt('%', #{p1.target_goods_name}, '%') or #{p1.target_goods_name} is null or #{p1.target_goods_name} = '')        "
            + "  and (t4.type = #{p1.type} or #{p1.type} is null or #{p1.type} = '')                                                                            "
            + "  and (date_format(t.c_time, '%Y-%m-%d') >= date_format(#{p1.start_time,jdbcType=DATE}, '%Y-%m-%d') or #{p1.start_time,jdbcType=DATE} is null)   "
            + "  and (date_format(t.c_time, '%Y-%m-%d') <= date_format(#{p1.over_time,jdbcType=DATE}, '%Y-%m-%d') or #{p1.over_time,jdbcType=DATE} is null)     "
            + "  and (t.status = #{p1.status} or #{p1.status} is null or #{p1.status} = '')                            "
            + "       ")
    IPage<BMaterialConvertVo> selectPage(Page page, @Param("p1") BMaterialConvertVo searchCondition);

    /**
     * 页面查询列表
     * @param searchCondition
     * @return
     */
    @Select("   "
            + common_select
            + "   where true                                                                                                           "
            + "  and (t4.is_effective =  #{p1.is_effective,jdbcType=BOOLEAN} or #{p1.is_effective,jdbcType=BOOLEAN} is null  )         "
            + "       ")
    List<BMaterialConvertVo> selectList(@Param("p1") BMaterialConvertVo searchCondition);

    /**
     * 按id查询
     * @param id
     * @return
     */
    @Select("  "
            + common_select
            + "  where t.id =  #{p1,jdbcType=INTEGER}           "
            + "      ")
    BMaterialConvertVo get(@Param("p1") int id);

    /**
     * 按id查询
     * @param id
     * @return
     */
    @Select("  "
            + common_select
            + "  where t4.id =  #{p1,jdbcType=INTEGER}           "
            + "      ")
    BMaterialConvertVo getByConvertId(@Param("p1") int id);

    /**
     * 查询列表
     */
    @Select("  "
            + common_select
            + "  where (t4.owner_id =  #{p1.owner_id,jdbcType=INTEGER} or  #{p1.owner_id,jdbcType=INTEGER} is null)                          "
            + "  and (t4.warehouse_id =  #{p1.warehouse_id,jdbcType=INTEGER}  or  #{p1.warehouse_id,jdbcType=INTEGER }  is null)             "
            + "  and (t4.id =  #{p1.material_convert_id,jdbcType=INTEGER} or  #{p1.material_convert_id,jdbcType=INTEGER} is null)            "
            + "  and (t4.is_effective =  #{p1.is_effective,jdbcType=BOOLEAN} or  #{p1.is_effective,jdbcType=BOOLEAN} is null)                "
            + "      ")
    List<BMaterialConvertVo> getList(@Param("p1") BMaterialConvertVo searchCondition);

    /**
     * 查询列表
     */
    @Select("  "
            + "      SELECT                                                                                                                     "
            + "             t.*                                                                                                      			"
            + "        FROM                                                                                                           			"
            + "   	       b_material_convert t                                                                                                 "
            + "  where true                                                                                                                     "
            + "  and t.is_latested = true                                                                                                       "
            + "  and (t.owner_id =  #{p1.owner_id,jdbcType=INTEGER} or  #{p1.owner_id,jdbcType=INTEGER} is null)                                "
            + "  and (t.sku_id =  #{p1.sku_id,jdbcType=INTEGER} or  #{p1.sku_id,jdbcType=INTEGER} is null)                                      "
            + "  and (t.warehouse_id =  #{p1.warehouse_id,jdbcType=INTEGER}  or  #{p1.warehouse_id,jdbcType=INTEGER }  is null)                 "
            + "  and (t.is_effective =  #{p1.is_effective,jdbcType=BOOLEAN} or  #{p1.is_effective,jdbcType=BOOLEAN} is null)                    "
            + "  and (t.id =  #{p1.material_convert_id,jdbcType=INTEGER} or  #{p1.material_convert_id,jdbcType=INTEGER} is null)                "
            + "      ")
    List<BMaterialConvertVo> getList1(@Param("p1") BMaterialConvertVo searchCondition);

    /**
     * 查询列表
     */
    @Select("  "
            + "      SELECT                                                                                                                     "
            + "             t.*                                                                                                      			"
            + "      FROM                                                                                                           			"
            + "   	       b_material_convert t                                                                                                 "
            + "      LEFT JOIN b_material_convert_detail t1 ON t.id = t1.material_convert_id                                                    "
            + "  where true                                                                                                                     "
            + "  and t.is_latested = true                                                                                                       "
            + "  and (t.owner_id =  #{p1.owner_id,jdbcType=INTEGER} or  #{p1.owner_id,jdbcType=INTEGER} is null)                                "
            + "  and t.type <> '0'                                                                                                              "
//            + "  and (t.sku_id =  #{p1.sku_id,jdbcType=INTEGER} or  #{p1.sku_id,jdbcType=INTEGER} is null)                                      "
            + "  and (t1.source_sku_id =  #{p1.source_sku_id,jdbcType=INTEGER} or  #{p1.source_sku_id,jdbcType=INTEGER} is null)                "
            + "  and (t.sku_id =  #{p1.sku_id,jdbcType=INTEGER} or  #{p1.sku_id,jdbcType=INTEGER} is null)                                      "
            + "  and (t.id <>  #{p1.material_convert_id,jdbcType=INTEGER} or  #{p1.material_convert_id,jdbcType=INTEGER} is null)               "
            + "  and (t.warehouse_id =  #{p1.warehouse_id,jdbcType=INTEGER}  or  #{p1.warehouse_id,jdbcType=INTEGER }  is null)                 "
            + "  and (t.is_effective =  #{p1.is_effective,jdbcType=BOOLEAN} or  #{p1.is_effective,jdbcType=BOOLEAN} is null)                    "
            + "      ")
    List<BMaterialConvertVo> getCheckList1(@Param("p1") BMaterialConvertVo searchCondition);

    /**
     * 查询列表
     */
    @Select("  "
            + "      SELECT                                                                                                                     "
            + "             t.*                                                                                                      			"
            + "        FROM                                                                                                           			"
            + "   	       b_material_convert t                                                                                                 "
            + "   	       left join b_material_convert_detail t1 on t.id = t1.material_convert_id                                              "
            + "  where true                                                                                                                     "
            + "  and (t.owner_id =  #{p1.owner_id,jdbcType=INTEGER} or  #{p1.owner_id,jdbcType=INTEGER} is null)                                "
            + "  and t.type <> '0'                                                                                                              "
            + "  and (t.sku_id =  #{p1.sku_id,jdbcType=INTEGER} or  #{p1.sku_id,jdbcType=INTEGER} is null)                                      "
            + "  and (t.id <>  #{p1.material_convert_id,jdbcType=INTEGER} or  #{p1.material_convert_id,jdbcType=INTEGER} is null)               "
            + "  and (t.warehouse_id =  #{p1.warehouse_id,jdbcType=INTEGER}  or  #{p1.warehouse_id,jdbcType=INTEGER }  is null)                 "
            + "  and (t1.source_sku_id =  #{p1.source_sku_id,jdbcType=INTEGER}  or  #{p1.source_sku_id,jdbcType=INTEGER }  is null)             "
            + "      ")
    List<BMaterialConvertNewVo> getCheckListNew(@Param("p1") BMaterialConvertNewVo searchCondition);

    /**
     * 查询列表
     */
    @Select("  "
            + "      SELECT                                                                                                                     "
            + "             t.*                                                                                                      			"
            + "        FROM                                                                                                           			"
            + "   	       b_material_convert t left join b_material_convert_detail t2 on t.id = t2.material_convert_id                         "
            + "  where true                                                                                                                     "
            + "  and (t.owner_id =  #{p1.owner_id,jdbcType=INTEGER} or  #{p1.owner_id,jdbcType=INTEGER} is null)                                "
//            + "  and (t.sku_id =  #{p1.target_sku_id,jdbcType=INTEGER} or  #{p1.target_sku_id,jdbcType=INTEGER} is null)                        "
            + "  and (t2.source_sku_id =  #{p1.source_sku_id,jdbcType=INTEGER} or  #{p1.source_sku_id,jdbcType=INTEGER} is null)                "
            + "  and (t2.id <>  #{p1.id,jdbcType=INTEGER} or  #{p1.id,jdbcType=INTEGER} is null)                                                "
            + "  and (t.warehouse_id =  #{p1.warehouse_id,jdbcType=INTEGER}  or  #{p1.warehouse_id,jdbcType=INTEGER }  is null)                 "
//            + "  and t.is_effective =  true and t2.is_effective =  true                                                                         "
            + "      ")
    List<BMaterialConvertVo> getCheckList2(@Param("p1") BMaterialConvertDetailVo searchCondition);

    /**
     * 查询列表
     */
    @Update("  "
            + "	UPDATE m_inventory tt1                                                                                                                                                                           "
            + "	INNER JOIN (                                                                                                                                                                                     "
            + "	SELECT                                                                                                                                                                                           "
            + "		tab1.id inventory_id,                                                                                                                                                                        "
            + "		sum( tab4.amount ) amount,                                                                                                                                                                   "
            + "		sum( tab4.actual_weight ) actual_weight,                                                                                                                                                     "
            + "		sum( tab4.amount ) / sum( tab4.actual_weight ) price                                                                                                                                         "
            + "	FROM                                                                                                                                                                                             "
            + "		m_inventory tab1                                                                                                                                                                             "
            + "		INNER JOIN (                                                                                                                                                                                 "
            + "	SELECT                                                                                                                                                                                           "
            + "		t2.warehouse_id,                                                                                                                                                                             "
            + "		t2.owner_id,                                                                                                                                                                                 "
            + "		t1.target_sku_id,                                                                                                                                                                               "
            + "		t1.source_sku_id                                                                                                                                                                                "
            + "	FROM                                                                                                                                                                                             "
            + "		b_material_convert_detail t1                                                                                                                                                                 "
            + "		INNER JOIN b_material_convert t2 ON t1.material_convert_id = t2.id                                                                                                                           "
            + "	WHERE                                                                                                                                                                                            "
            + "		t1.is_effective = TRUE                                                                                                                                                                       "
            + "		AND t2.is_effective = TRUE                                                                                                                                                                   "
            + "		) tab2 ON tab1.sku_id = tab2.target_sku_id                                                                                                                                                      "
            + "		AND tab1.warehouse_id = tab2.warehouse_id                                                                                                                                                    "
            + "		AND tab1.owner_id = tab2.owner_id                                                                                                                                                            "
            + "		LEFT JOIN (                                                                                                                                                                                  "
            + "	SELECT                                                                                                                                                                                           "
            + "		t1.sku_id,                                                                                                                                                                                   "
            + "		t1.sku_code,                                                                                                                                                                                 "
            + "		t1.owner_id,                                                                                                                                                                                 "
            + "		t1.warehouse_id,                                                                                                                                                                             "
            + "		sum( t1.actual_weight ) actual_weight,                                                                                                                                                       "
            + "		sum( t1.actual_weight * ifnull( t2.price, ifnull( t3.price, 0 ) ) ) amount,                                                                                                                  "
            + "		sum( t1.actual_weight * ifnull( t2.price, ifnull( t3.price, 0 ) ) ) / sum( t1.actual_weight ) price                                                                                          "
            + "	FROM                                                                                                                                                                                             "
            + "		b_in t1                                                                                                                                                                                      "
            + "		LEFT JOIN (SELECT *  FROM (SELECT * FROM b_goods_price ORDER BY c_time desc LIMIT 10000) t GROUP BY sku_id,DATE_FORMAT( price_dt, '%Y-%m-%d' )) t2 ON t1.sku_id = t2.sku_id                  "
            + "		AND DATE_FORMAT( t1.u_time, '%Y-%m-%d' ) = DATE_FORMAT( t2.price_dt, '%Y-%m-%d' )                                                                                                            "
            + "		LEFT JOIN (                                                                                                                                                                                  "
            + "	        SELECT                                                                                                                                                                                   "
            + "	        	sku_id,                                                                                                                                                                              "
            + "	        	price,                                                                                                                                                                               "
            + "	        	price_dt,                                                                                                                                                                            "
            + "	        	c_time,                                                                                                                                                                              "
            + "	        	row_number ( ) over ( PARTITION BY sku_id ORDER BY price_dt DESC, c_time DESC ) AS row_num                                                                                           "
            + "	        FROM                                                                                                                                                                                     "
            + "	        	b_goods_price                                                                                                                                                                        "
            + "	        	) t3 ON t3.sku_id = t1.sku_id                                                                                                                                                        "
            + "	        	AND t3.row_num = 1                                                                                                                                                                   "
            + "	WHERE                                                                                                                                                                                            "
            + "		DATE_FORMAT( t1.u_time, '%Y-%m-%d' ) >= DATE_FORMAT( DATE_SUB( now( ), INTERVAL #{p1,jdbcType=INTEGER} DAY ), '%Y-%m-%d' )                                                                   "
            + "		AND t1.status = '2'                                                                                                                                                                          "
            + "	GROUP BY                                                                                                                                                                                         "
            + "		t1.sku_id,                                                                                                                                                                                   "
            + "		t1.warehouse_id,                                                                                                                                                                             "
            + "		t1.owner_id                                                                                                                                                                                  "
            + "		) tab4 ON tab2.warehouse_id = tab4.warehouse_id                                                                                                                                              "
            + "		AND tab2.owner_id = tab4.owner_id                                                                                                                                                            "
            + "		AND tab2.source_sku_id = tab4.sku_id                                                                                                                                                            "
            + "	WHERE                                                                                                                                                                                            "
            + "		tab1.sku_id = tab2.target_sku_id                                                                                                                                                                "
            + "		AND tab1.warehouse_id = tab2.warehouse_id                                                                                                                                                    "
            + "		AND tab1.owner_id = tab2.owner_id                                                                                                                                                            "
            + "	GROUP BY                                                                                                                                                                                         "
            + "		tab1.warehouse_id,                                                                                                                                                                           "
            + "		tab1.owner_id,                                                                                                                                                                               "
            + "		tab1.sku_id                                                                                                                                                                                  "
            + "		) tt2 ON tt1.id = tt2.inventory_id                                                                                                                                                           "
            + "		SET tt1.price = IFNULL(tt2.price,0)                                                                                                                                                          "
            + "	WHERE                                                                                                                                                                                            "
            + "		tt1.id = tt2.inventory_id                                                                                                                                                                    "
             + "      ")
    public void reCallPrice(@Param("p1") Integer days);

    @Select(selectConvertPricePage)
    IPage<BMaterialConvertPriceVo> selectConvertPricePage(@Param("p1") BMaterialConvertPriceVo searchCondition, Page<BMaterialConvertEntity> pageCondition);

    @Select("<script>                                                                                                  "
            + "select                                                                                                  "
            + " tab1.business_name,                                                                                    "
            + " tab1.owner_simple_name,                                                                                "
            + " tab1.warehouse_simple_name,                                                                            "
            + " tab1.goods_name,                                                                                       "
            + " tab1.spec,                                                                                             "
            + " tab1.sku_code,                                                                                         "
            + " tab1.dt,                                                                                               "
            + " ifnull(tab1.price, 0) price,                                                                           "
            + " tab1.dt,                                                                                               "
            + " tab1.qty,                                                                                              "
            + " tab1.amount,                                                                                           "
            + " @row_num:= @row_num+ 1 as no                                                                           "
            + " from (                                                                                                 "
            + selectConvertPricePage
            + " ) tab1                                                                                                 "
            + " ,(select @row_num:=0) t5                                                                               "
            + "   <if test='p1.ids != null and p1.ids.length !=0 ' >                                                   "
            + "    where tab1.id in                                                                                    "
            + "        <foreach collection='p1.ids' item='item' index='index' open='(' separator=',' close=')'>        "
            + "         #{item}                                                                                        "
            + "        </foreach>                                                                                      "
            + "   </if>                                                                                                "
            + "</script>                                                                                              "
    )
    List<BMaterialConvertPriceExportVo> exportList(@Param("p1") BMaterialConvertPriceVo searchCondition);

    @Select(
            "select                                                                                                    "
                    + " sum(tab1.qty) qty,                                                                                 "
                    + " sum(tab1.amount) amount                                                                             "
                    + " from (                                                                                         "
                    + selectConvertPricePage
                    + " ) tab1                                                                                         "
    )
    BMaterialConvertPriceVo selectConvertPriceSum(@Param("p1") BMaterialConvertPriceVo searchCondition);


    @Select("  SELECT                                                                                                  "
            +    "      t1.id,                                                                                         "
            +    "      concat(t1.name,' v',t1.data_version) convert_name,                                              "
            +    "  	t7.count,                                                                                      "
            +    "      t1.`code`,                                                                                     "
            +    "  	t1.data_version,                                                                               "
            +    "  	t1.`name`,                                                                                     "
            +    "  	t2.`name` owner_name,                                                                          "
            +    "  	t3.`name` warehouse_name,                                                                      "
            +    "  	t1.is_effective,                                                                               "
            +    "  	t4.label type_name,                                                                            "
            +    "  	t6.`code` target_goods_code,                                                                   "
            +    "  	t6.`name` target_goods_name,                                                                   "
            +    "  	t1.convert_time,                                                                               "
            +    "      t1.warehouse_id,                                                                               "
            +    "      t5.spec target_spec,                                                                           "
            +    "      t1.c_time,                                                                                     "
            +    "      t1.u_time,                                                                                     "
            +    "      t8.name c_name,                                                                                "
            +    "      t9.name u_name                                                                                 "
            +    "  FROM                                                                                               "
            +    "    b_material_convert t1                                                                            "
            +    "  LEFT JOIN m_owner t2 ON t1.owner_id = t2.id                                                        "
            +    "  LEFT JOIN m_warehouse t3 ON t3.id = t1.warehouse_id                                                "
            +    "  LEFT JOIN v_dict_info t4 on t4.code ='" + DictConstant.DICT_B_MATERIAL_CONVERT_TYPE + "' and t4.dict_value = t1.type"
            +    "  LEFT JOIN m_goods_spec t5 ON t5.id = t1.sku_id                                                     "
            +    "  LEFT JOIN m_goods t6 ON t6.id = t5.goods_id                                                        "
            +    "  LEFT JOIN (select count(1) count, material_convert_id from b_material_convert_detail group by material_convert_id) t7 on t7.material_convert_id = t1.id"
            +    "  LEFT JOIN m_staff t8 ON t1.c_id = t8.id                                                            "
            +    "  LEFT JOIN m_staff t9 ON t1.u_id = t9.id                                                            "
            +    "  WHERE TRUE                                                                                         "
            + "  and is_latested = true"
            + "  and (t1.is_effective =  #{p1.is_effective,jdbcType=BOOLEAN} or #{p1.is_effective,jdbcType=BOOLEAN} is null  )"
            + "  and (t1.code like concat('%', #{p1.code}, '%') or #{p1.code} is null or #{p1.code} = '')              "
            + "  and (t1.owner_id = #{p1.owner_id} or #{p1.owner_id} is null)                                          "
            + "  and (t1.warehouse_id = #{p1.warehouse_id} or #{p1.warehouse_id} is null)                              "
            + "  and (t6.`name` LIKE CONCAt('%', #{p1.target_goods_name}, '%') or t5.`name` LIKE CONCAt('%', #{p1.target_goods_name}, '%') "
            + "  or t1.sku_code LIKE CONCAt('%', #{p1.target_goods_name}, '%') or t6.`code` LIKE CONCAt('%', #{p1.target_goods_name}, '%') or #{p1.target_goods_name} is null or #{p1.target_goods_name} = '')"
            + "  and (t1.type = #{p1.type} or #{p1.type} is null or #{p1.type} = '') "
            + "  and exists (select 1 from b_material_convert_detail tt1 "
            + "     LEFT JOIN m_goods_spec tt2 ON tt2.id = tt1.source_sku_id "
            + "     LEFT JOIN m_goods tt3 ON tt3.id = tt2.goods_id                    "
            + "     where tt1.material_convert_id = t1.id and concat(tt3.name,tt3.code,tt2.code,tt2.spec) like CONCAt('%', #{p1.source_goods_name}, '%') or #{p1.source_goods_name} is null or #{p1.source_goods_name} = '')"
//            + "  and (date_format(t.c_time, '%Y-%m-%d') >= date_format(#{p1.start_time,jdbcType=DATE}, '%Y-%m-%d') or #{p1.start_time,jdbcType=DATE} is null)"
//            + "  and (date_format(t.c_time, '%Y-%m-%d') <= date_format(#{p1.over_time,jdbcType=DATE}, '%Y-%m-%d') or #{p1.over_time,jdbcType=DATE} is null)  "
//            + "  and (t.status = #{p1.status} or #{p1.status} is null or #{p1.status} = '')                            "
    )
    IPage<BMaterialConvert1Vo> selectPage1(Page page, @Param("p1") BMaterialConvertVo searchCondition);


    /**
     * 根据ID 查询
     * @param id
     * @return
     */
    @Select("  SELECT                                                                                                  "
            +    "      t1.id,                                                                                         "
            +    "  	t7.count,                                                                                      "
            +    "      t1.`code`,                                                                                     "
            +    "  	t1.data_version,                                                                               "
            +    "  	t1.`name`,                                                                                     "
            +    "  	ifnull(t2.short_name , t2.`name`) owner_name,                                                  "
            +    "  	t1.is_effective,                                                                               "
            +    "  	t4.label type_name,                                                                            "
            +    "  	t5.`code` sku_code,                                                                            "
            +    "  	t6.`name` goods_name,                                                                          "
            +    "      t5.spec,                                                                                        "
            +    "  	t1.convert_time,                                                                               "
            +    "      t1.warehouse_id,                                                                               "
            +    "      t1.sku_id,                                                                                     "
            +    "      t1.owner_id,                                                                                   "
            +    "      t1.type,                                                                                       "
            +    "      t7.source_goods_name,                                                                          "
            +    "      t7.source_sku_code,                                                                            "
            +    "      t7.source_sku_id,                                                                              "
            +    "      t7.source_spec,                                                                                "
            +    "      t7.calc,                                                                                       "
            +    "      t1.c_time,                                                                                     "
            +    "      t1.u_time,                                                                                     "
            +    "      t8.name c_name,                                                                                "
            +    "      t9.name u_name                                                                                 "
            +    "  FROM                                                                                               "
            +    "    b_material_convert t1                                                                            "
            +    "  LEFT JOIN m_owner t2 ON t1.owner_id = t2.id                                                        "
            +    "  LEFT JOIN v_dict_info t4 on t4.code ='" + DictConstant.DICT_B_MATERIAL_CONVERT_TYPE + "' and t4.dict_value = t1.type"
            +    "  LEFT JOIN m_goods_spec t5 ON t5.id = t1.sku_id                                                     "
            +    "  LEFT JOIN m_goods t6 ON t6.id = t5.goods_id                                                        "
            +    "  LEFT JOIN (select                                                                                  "
            +    "      count(1) count,                                                                                "
            +    "      material_convert_id,                                                                           "
            +    "      tt3.name source_goods_name,                                                                    "
            +    "      tt2.code source_sku_code,                                                                      "
            +    "      tt1.source_sku_id,                                                                             "
            +    "      tt1.calc,                                                                                      "
            +    "      tt2.spec source_spec                                                                           "
            +    "  FROM                                                                                               "
            +    "    b_material_convert_detail tt1                                                                    "
            +    "  LEFT JOIN m_goods_spec tt2 on tt2.id = tt1.source_sku_id                                           "
            +    "  LEFT JOIN m_goods tt3 on tt2.goods_id = tt3.id                                                     "
            +    "    group by tt1.material_convert_id  ) t7 on t7.material_convert_id = t1.id                         "
            +    "  LEFT JOIN m_staff t8 ON t1.c_id = t8.id                                                            "
            +    "  LEFT JOIN m_staff t9 ON t1.u_id = t9.id                                                            "
            +    " where t1.id = #{id}                                                                                 "
    )
    BMaterialConvertVo getConvert(Integer id);

    @Select("  SELECT                                                                                                  "
            +    "      t1.id,                                                                                         "
            +    "      t1.owner_id,                                                                                   "
            +    "      concat(t1.name,' v',t1.data_version) convert_name,                                             "
            +    "  	t7.count,                                                                                      "
            +    "      t1.`code`,                                                                                     "
            +    "  	t1.data_version,                                                                               "
            +    "  	t1.`name`,                                                                                     "
            +    "  	t2.`name` owner_name,                                                                          "
            +    "  	t3.`name` warehouse_name,                                                                      "
            +    "  	t1.is_effective,                                                                               "
            +    "  	t4.label type_name,                                                                            "
            +    "  	t5.`code` target_goods_code,                                                                   "
            +    "  	t6.`name` target_goods_name,                                                                   "
            +    "  	t11.`code` source_goods_code,                                                                  "
            +    "  	t12.`name` source_goods_name,                                                                  "
            +    "  	t11.`spec` source_spec,                                                                        "
            +    "  	t1.convert_time,                                                                               "
            +    "      t1.warehouse_id,                                                                               "
            +    "      t5.spec target_spec,                                                                           "
            +    "      t1.c_time,                                                                                     "
            +    "      t1.u_time,                                                                                     "
            +    "      t8.name c_name,                                                                                "
            +    "      t10.calc,                                                                                      "
            +    "      t10.source_sku_id,                                                                             "
            +    "      t10.target_sku_id sku_id,                                                                             "
            +    "      t10.id detail_id,                                                                              "
            +    "      t9.name u_name                                                                                 "
            +    "  FROM                                                                                               "
            +    "    b_material_convert t1                                                                            "
            +    "  LEFT JOIN m_owner t2 ON t1.owner_id = t2.id                                                        "
            +    "  LEFT JOIN m_warehouse t3 ON t3.id = t1.warehouse_id                                                "
            +    "  LEFT JOIN v_dict_info t4 on t4.code ='" + DictConstant.DICT_B_MATERIAL_CONVERT_TYPE + "' and t4.dict_value = t1.type"
            +    "  LEFT JOIN m_goods_spec t5 ON t5.id = t1.sku_id                                                     "
            +    "  LEFT JOIN m_goods t6 ON t6.id = t5.goods_id                                                        "
            +    "  LEFT JOIN (select count(1) count, material_convert_id from b_material_convert_detail group by material_convert_id) t7 on t7.material_convert_id = t1.id"
            +    "  LEFT JOIN m_staff t8 ON t1.c_id =  t8.id                                                            "
            +    "  LEFT JOIN m_staff t9 ON t1.u_id = t9.id                                                            "
            +    "  LEFT JOIN b_material_convert_detail t10 ON t1.id = t10.material_convert_id                         "
            +    "  LEFT JOIN m_goods_spec t11 ON t11.id = t10.source_sku_id                                           "
            +    "  LEFT JOIN m_goods t12 ON t12.id = t11.goods_id                                                     "
            +    "  WHERE TRUE                                                                                         "
            + "  and is_latested = true"
            + "  and (t1.is_effective =  #{p1.is_effective,jdbcType=BOOLEAN} or #{p1.is_effective,jdbcType=BOOLEAN} is null  )"
            + "  and (t1.code like concat('%', #{p1.code}, '%') or #{p1.code} is null or #{p1.code} = '')               "
            + "  and (t1.owner_id = #{p1.owner_id} or #{p1.owner_id} is null)                                          "
            + "  and (t1.warehouse_id = #{p1.warehouse_id} or #{p1.warehouse_id} is null)                              "
            + "  and (t6.`name` LIKE CONCAt('%', #{p1.target_goods_name}, '%') or t5.`name` LIKE CONCAt('%', #{p1.target_goods_name}, '%') "
            + "  or t1.sku_code " +
            "LIKE CONCAt('%', #{p1.target_goods_name}, '%') or t6.`code` LIKE CONCAt('%', #{p1.target_goods_name}, '%') or #{p1.target_goods_name} is null or #{p1.target_goods_name} = '')"
            + "  and (t1.type = #{p1.type} or #{p1.type} is null or #{p1.type} = '') "
            + "  and exists (select 1 from b_material_convert_detail tt1 "
            + "     LEFT JOIN m_goods_spec tt2 ON tt2.id = tt1.source_sku_id "
            + "     LEFT JOIN m_goods tt3 ON tt3.id = tt2.goods_id                    "
            + "     where tt1.material_convert_id = t1.id and concat(tt3.name,tt3.code,tt2.code,tt2.spec) like CONCAt('%', #{p1.source_goods_name}, '%') or #{p1.source_goods_name} is null or #{p1.source_goods_name} = '')"
//            + "  and (date_format(t.c_time, '%Y-%m-%d') >= date_format(#{p1.start_time,jdbcType=DATE}, '%Y-%m-%d') or #{p1.start_time,jdbcType=DATE} is null)"
//            + "  and (date_format(t.c_time, '%Y-%m-%d') <= date_format(#{p1.over_time,jdbcType=DATE}, '%Y-%m-%d') or #{p1.over_time,jdbcType=DATE} is null)  "
//            + "  and (t.status = #{p1.status} or #{p1.status} is null or #{p1.status} = '')                            "
    )
    IPage<BMaterialConvert1Vo> selectPage2(Page<BMaterialConvertEntity> pageCondition,@Param("p1") BMaterialConvertVo searchCondition);

    @Select("  SELECT                                                                                                  "
            +    "      t1.id,                                                                                         "
            +    "      t1.owner_id,                                                                                   "
            +    "      concat(t1.name,' v',t1.data_version) convert_name,                                             "
            +    "  	t7.count,                                                                                      "
            +    "      t1.`code`,                                                                                     "
            +    "  	t1.data_version,                                                                               "
            +    "  	t1.`name`,                                                                                     "
            +    "  	ifnull(t2.short_name , t2.`name`) owner_name,                                                  "
            +    "  	t3.`name` warehouse_name,                                                                      "
            +    "  	t1.is_effective,                                                                               "
            +    "  	t4.label type_name,                                                                            "
            +    "  	t5.`code` target_sku_code,                                                                   "
            +    "  	t6.`name` target_goods_name,                                                                   "
            +    "  	t11.`code` source_sku_code,                                                                  "
            +    "  	t12.`name` source_goods_name,                                                                  "
            +    "  	t11.`spec` source_spec,                                                                        "
            +    "  	t1.convert_time,                                                                               "
            +    "      t1.warehouse_id,                                                                               "
            +    "      t5.spec target_spec,                                                                           "
            +    "      t1.c_time,                                                                                     "
            +    "      t1.u_time,                                                                                     "
            +    "      t8.name c_name,                                                                                "
            +    "      t10.calc,                                                                                      "
            +    "      t10.source_sku_id,                                                                             "
            +    "      t10.target_sku_id sku_id,                                                                             "
            +    "      t10.id detail_id,                                                                              "
            +    "      t9.name u_name                                                                                 "
            +    "  FROM                                                                                               "
            +    "    b_material_convert t1                                                                            "
            +    "  LEFT JOIN m_owner t2 ON t1.owner_id = t2.id                                                        "
            +    "  LEFT JOIN m_warehouse t3 ON t3.id = t1.warehouse_id                                                "
            +    "  LEFT JOIN v_dict_info t4 on t4.code ='" + DictConstant.DICT_B_MATERIAL_CONVERT_TYPE + "' and t4.dict_value = t1.type"
            +    "  LEFT JOIN m_goods_spec t5 ON t5.id = t1.sku_id                                                     "
            +    "  LEFT JOIN m_goods t6 ON t6.id = t5.goods_id                                                        "
            +    "  LEFT JOIN (select count(1) count, material_convert_id from b_material_convert_detail group by material_convert_id) t7 on t7.material_convert_id = t1.id"
            +    "  LEFT JOIN m_staff t8 ON t1.c_id =  t8.id                                                            "
            +    "  LEFT JOIN m_staff t9 ON t1.u_id = t9.id                                                            "
            +    "  LEFT JOIN b_material_convert_detail t10 ON t1.id = t10.material_convert_id                         "
            +    "  LEFT JOIN m_goods_spec t11 ON t11.id = t10.source_sku_id                                           "
            +    "  LEFT JOIN m_goods t12 ON t12.id = t11.goods_id                                                     "
            +    "  WHERE TRUE                                                                                         "
            + "  and is_latested = true"
            + "  and (t1.is_effective =  #{p1.is_effective,jdbcType=BOOLEAN} or #{p1.is_effective,jdbcType=BOOLEAN} is null  )"
            + "  and (t1.code like concat('%', #{p1.code}, '%') or #{p1.code} is null or #{p1.code} = '')               "
            + "  and (t1.owner_id = #{p1.owner_id} or #{p1.owner_id} is null)                                          "
            + "  and (t1.warehouse_id = #{p1.warehouse_id} or #{p1.warehouse_id} is null)                              "
            + "  and (t6.`name` LIKE CONCAt('%', #{p1.target_goods_name}, '%') or t5.`name` LIKE CONCAt('%', #{p1.target_goods_name}, '%') "
            + "  or t1.sku_code " +
            "LIKE CONCAt('%', #{p1.target_goods_name}, '%') or t6.`code` LIKE CONCAt('%', #{p1.target_goods_name}, '%') or #{p1.target_goods_name} is null or #{p1.target_goods_name} = '')"
            + "  and (t1.type = #{p1.type} or #{p1.type} is null or #{p1.type} = '') "
            + "  and exists (select 1 from b_material_convert_detail tt1 "
            + "     LEFT JOIN m_goods_spec tt2 ON tt2.id = tt1.source_sku_id "
            + "     LEFT JOIN m_goods tt3 ON tt3.id = tt2.goods_id                    "
            + "     where tt1.material_convert_id = t1.id and concat(tt3.name,tt3.code,tt2.code,tt2.spec) like CONCAt('%', #{p1.source_goods_name}, '%') or #{p1.source_goods_name} is null or #{p1.source_goods_name} = '')"
//            + "  and (date_format(t.c_time, '%Y-%m-%d') >= date_format(#{p1.start_time,jdbcType=DATE}, '%Y-%m-%d') or #{p1.start_time,jdbcType=DATE} is null)"
//            + "  and (date_format(t.c_time, '%Y-%m-%d') <= date_format(#{p1.over_time,jdbcType=DATE}, '%Y-%m-%d') or #{p1.over_time,jdbcType=DATE} is null)  "
//            + "  and (t.status = #{p1.status} or #{p1.status} is null or #{p1.status} = '')                            "
    )
    IPage<BMaterialConvertNewVo> selectPageNew(Page<BMaterialConvertEntity> pageCondition,@Param("p1") BMaterialConvertNewVo searchCondition);

    /**
     * 部分导出
     * @param searchCondition
     * @return
     */
    @Select("<script>"
            + export_sql
            + " where t1.id in                                                                                          "
            + "        <foreach collection='p1' item='item' index='index' open='(' separator=',' close=')'>             "
            + "         #{item.id}                                                                                      "
            + "        </foreach>                                                                                       "
            + "</script>")
    List<BMaterialConvertExportVo> selectExportList(@Param("p1") List<BMaterialConvertNewVo> searchCondition);

    /**
     * 全部导出
     * @param searchCondition
     * @return
     */
    @Select(""
            + export_sql
            +    "  WHERE TRUE                                                                                         "
            + "  and is_latested = true"
            + "  and (t1.is_effective =  #{p1.is_effective,jdbcType=BOOLEAN} or #{p1.is_effective,jdbcType=BOOLEAN} is null  )"
            + "  and (t1.code like concat('%', #{p1.code}, '%') or #{p1.code} is null or #{p1.code} = '')               "
            + "  and (t1.owner_id = #{p1.owner_id} or #{p1.owner_id} is null)                                          "
            + "  and (t1.warehouse_id = #{p1.warehouse_id} or #{p1.warehouse_id} is null)                              "
            + "  and (t6.`name` LIKE CONCAt('%', #{p1.target_goods_name}, '%') or t5.`name` LIKE CONCAt('%', #{p1.target_goods_name}, '%') "
            + "  or t1.sku_code " +
            "LIKE CONCAt('%', #{p1.target_goods_name}, '%') or t6.`code` LIKE CONCAt('%', #{p1.target_goods_name}, '%') or #{p1.target_goods_name} is null or #{p1.target_goods_name} = '')"
            + "  and (t1.type = #{p1.type} or #{p1.type} is null or #{p1.type} = '') "
            + "  and exists (select 1 from b_material_convert_detail tt1 "
            + "     LEFT JOIN m_goods_spec tt2 ON tt2.id = tt1.source_sku_id "
            + "     LEFT JOIN m_goods tt3 ON tt3.id = tt2.goods_id                    "
            + "     where tt1.material_convert_id = t1.id and concat(tt3.name,tt3.code,tt2.code,tt2.spec) like CONCAt('%', #{p1.source_goods_name}, '%') or #{p1.source_goods_name} is null or #{p1.source_goods_name} = '')"
            + "")
    List<BMaterialConvertExportVo> selectExportAllList(@Param("p1") BMaterialConvertNewVo searchCondition);

    /**
     * 全部导出
     * @param searchCondition
     * @return
     */
    @Select(""
            + " select count(1)                                                                                        "
            +    "  FROM                                                                                               "
            +    "    b_material_convert t1                                                                            "
            +    "  LEFT JOIN m_goods_spec t5 ON t5.id = t1.sku_id                                                     "
            +    "  LEFT JOIN m_goods t6 ON t6.id = t5.goods_id                                                        "
            +    "  LEFT JOIN b_material_convert_detail t10 ON t1.id = t10.material_convert_id                         "
            +    "  LEFT JOIN m_goods_spec t11 ON t11.id = t10.source_sku_id                                           "
            +    "  LEFT JOIN m_goods t12 ON t12.id = t11.goods_id                                                     "
            +    "  WHERE TRUE                                                                                         "
            + "  and is_latested = true                                                                                "
            + "  and (t1.is_effective =  #{p1.is_effective,jdbcType=BOOLEAN} or #{p1.is_effective,jdbcType=BOOLEAN} is null  )"
            + "  and (t1.code like concat('%', #{p1.code}, '%') or #{p1.code} is null or #{p1.code} = '')               "
            + "  and (t1.owner_id = #{p1.owner_id} or #{p1.owner_id} is null)                                          "
            + "  and (t1.warehouse_id = #{p1.warehouse_id} or #{p1.warehouse_id} is null)                              "
            + "  and (t6.`name` LIKE CONCAt('%', #{p1.target_goods_name}, '%') or t5.`name` LIKE CONCAt('%', #{p1.target_goods_name}, '%') "
            + "  or t1.sku_code " +
            "LIKE CONCAt('%', #{p1.target_goods_name}, '%') or t6.`code` LIKE CONCAt('%', #{p1.target_goods_name}, '%') or #{p1.target_goods_name} is null or #{p1.target_goods_name} = '')"
            + "  and (t1.type = #{p1.type} or #{p1.type} is null or #{p1.type} = '') "
            + "  and exists (select 1 from b_material_convert_detail tt1 "
            + "     LEFT JOIN m_goods_spec tt2 ON tt2.id = tt1.source_sku_id "
            + "     LEFT JOIN m_goods tt3 ON tt3.id = tt2.goods_id                    "
            + "     where tt1.material_convert_id = t1.id and concat(tt3.name,tt3.code,tt2.code,tt2.spec) like CONCAt('%', #{p1.source_goods_name}, '%') or #{p1.source_goods_name} is null or #{p1.source_goods_name} = '')"
            + "")
    int getExportAllNum(@Param("p1") BMaterialConvertNewVo searchCondition);
}
