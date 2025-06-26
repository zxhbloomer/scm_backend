package com.xinyirun.scm.core.app.mapper.sys.config.dict;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xinyirun.scm.bean.app.vo.sys.config.dict.AppNutuiNameAndValue;
import com.xinyirun.scm.bean.app.vo.sys.config.dict.AppSDictDataVo;
import com.xinyirun.scm.bean.entity.sys.config.dict.SDictDataEntity;
import com.xinyirun.scm.common.constant.DictConstant;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>
 * 字典数据表 Mapper 接口
 * </p>
 *
 * @author htt
 * @since 2021-12-20
 */
@Repository
public interface AppSDictDataMapper extends BaseMapper<SDictDataEntity> {

    String common_select = "  "
            + "  SELECT                                                                 "
            + "       t1.id,                                                            "
            + "       t1.dict_type_id,                                                  "
            + "       t1.sort,                                                          "
            + "       t1.label,                                                         "
            + "       t1.dict_value ,                                                   "
            + "       t1.descr,                                                         "
            + "       t1.is_del,                                                        "
            + "       t1.c_id,                                                          "
            + "       t1.c_time,                                                        "
            + "       t1.u_id,                                                          "
            + "       t1.u_time,                                                        "
            + "       t1.extra1,                                                        "
            + "       t1.extra2,                                                        "
            + "       t1.extra3,                                                        "
            + "       t1.extra4,                                                        "
            + "       t1.dbversion,                                                     "
            + "       t2.name  dictTypeName ,                                           "
            + "       t2.code  dictTypeCode,                                            "
            + "       t2.descr dict_type_descr,                                         "
            + "       t2.is_del dictTypeIsdel,                                          "
            + "       t3.max_sort,                                                      "
            + "       t3.min_sort,                                                      "
            + "       t1.dict_value as table_name,                                      "
            + "       t1.label as table_comment,                                        "
            + "       t1.extra1 as column_name,                                         "
            + "       t1.extra2 as column_comment                                       "
            + "  FROM                                                                   "
            + "  	s_dict_data AS t1                                                   "
            + "  	LEFT JOIN s_dict_type AS t2 ON t1.dict_type_id = t2.id              "
            + "  	INNER JOIN (                                                        "
            + "  		SELECT                                                          "
            + "  			count(1) - 1 AS max_sort,                                   "
            + "  			0 AS min_sort,                                              "
            + "  			subt1.dict_type_id                                          "
            + "  		FROM                                                            "
            + "  			s_dict_data subt1                                           "
            + "  		GROUP BY                                                        "
            + "  			subt1.dict_type_id                                          "
            + "  	) t3 ON t1.dict_type_id = t3.dict_type_id                           "
            + "                                                                         "
            ;

    /**
     * 页面查询列表
     */
    @Select("    "
            + common_select
            + "  where true "
            + "    and (t2.code = CONCAT ('',#{p1.dictTypeCode,jdbcType=VARCHAR},'') or #{p1.dictTypeCode,jdbcType=VARCHAR} is null) "
            + "    and (t2.name like CONCAT ('%',#{p1.dictTypeName,jdbcType=VARCHAR},'%') or #{p1.dictTypeName,jdbcType=VARCHAR} is null) "
            + "    and (t1.label like CONCAT ('%',#{p1.label,jdbcType=VARCHAR},'%') or #{p1.label,jdbcType=VARCHAR} is null) "
            + "      ")
    List<AppSDictDataVo> selectPage( @Param("p1") AppSDictDataVo searchCondition );

    /**
     * 页面查询列表
     */
    @Select("    "
            + "   select t1.code,                                                                                           "
            + "          t1.label,                                                                                          "
            + "          t1.dict_value as `value`                                                                           "
            + "     from s_dict_data t1                                                                                     "
            + "    where true                                                                                               "
            + "      and (t1.code =  #{p1.code, jdbcType=VARCHAR} or #{p1.code, jdbcType=VARCHAR} is null )                 "
            + "          ")
    List<AppNutuiNameAndValue> selectNutuiNameAndValue(@Param("p1") AppNutuiNameAndValue searchCondition );
}
