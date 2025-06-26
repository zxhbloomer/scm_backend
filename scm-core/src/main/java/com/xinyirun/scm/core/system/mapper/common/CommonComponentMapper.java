package com.xinyirun.scm.core.system.mapper.common;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xinyirun.scm.bean.system.vo.common.component.DictConditionVo;
import com.xinyirun.scm.bean.system.vo.common.component.DictGroupVo;
import com.xinyirun.scm.bean.system.vo.common.component.NameAndValueVo;
import com.xinyirun.scm.common.constant.DictConstant;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>
 * 获取下拉选项的 Mapper 接口
 * </p>
 *
 * @author zxh
 * @since 2019-07-13
 */
@Repository
public interface CommonComponentMapper extends BaseMapper<NameAndValueVo> {

    @Select( "   "
        + "  SELECT                                                              "
        + "       t2.label as `name`,                                            "
        + "       t2.dict_value as `value`,                                      "
        + "       t1.`name` as dict_type_code,                                   "
        + "       t2.`extra1` as extra1,                                         "
        + "       t2.id as dict_data_id                                          "
        + "    FROM                                                              "
        + "       s_dict_type t1                                                 "
        + "       INNER JOIN s_dict_data t2 ON t1.id = t2.dict_type_id           "
        + "       AND t1.is_del = "+ DictConstant.DICT_SYS_DELETE_MAP_NO + " "
        + "       AND t2.is_del = "+ DictConstant.DICT_SYS_DELETE_MAP_NO + " "
        + "       and t1.code = #{p1}                                             "
        + "     order by t2.sort    "
        + "      ")
    List<NameAndValueVo> getSelectDictDataNormal(@Param("p1") String dict_type_code);

    @Select( "   "
        + "  SELECT                                                                                    "
        + "       t2.extra1 as label_code,                                                             "
        + "       t2.extra2 as label,                                                                  "
        + "       JSON_ARRAYAGG(JSON_OBJECT('name',t2.label,'value',t2.dict_value )) as options        "
        + "    FROM                                                                                    "
        + "       s_dict_type t1                                                                       "
        + "       INNER JOIN s_dict_data t2 ON t1.id = t2.dict_type_id                                 "
        + "       AND t1.is_del = "+ DictConstant.DICT_SYS_DELETE_MAP_NO + "                    "
        + "       AND t2.is_del = "+ DictConstant.DICT_SYS_DELETE_MAP_NO + "                    "
        + "       and t1.code = #{p1}                                                                  "
        + "     group by t2.extra1                                                                     "
        + "      ")
    List<DictGroupVo> getSelectDictGroupDataNormal(@Param("p1") String dict_type_code);

    /**
     * 下拉选项卡：按参数查询，包含filter
     * @param condition
     * @return
     */
    @Select( "  <script>  "
        + "  SELECT                                                              "
        + "       t2.label as `name`,                                            "
        + "       t2.dict_value as `value`,                                      "
        + "       t1.`name` as dict_type_code,                                   "
        + "       t2.`extra1` as extra1,                                         "
        + "       t2.id as dict_data_id                                          "
        + "    FROM                                                              "
        + "       s_dict_type t1                                                 "
        + "       INNER JOIN s_dict_data t2 ON t1.id = t2.dict_type_id           "
        + "       AND t1.is_del = "+ DictConstant.DICT_SYS_DELETE_MAP_NO+" "
        + "       AND t2.is_del = "+ DictConstant.DICT_SYS_DELETE_MAP_NO+" "
        + "       AND t1.code = #{p1.para,jdbcType=VARCHAR}                      "
        + "   <if test='p1.filter_para != null and p1.filter_para.length!=0' >   "
        + "    and t2.dict_value not in                                              "
        + "        <foreach collection='p1.filter_para' item='item' index='index' open='(' separator=',' close=')'>"
        + "         #{item}  "
        + "        </foreach>   "
        + "   </if>   "
        + "     order by t2.sort    "
        + "  </script>     ")
    List<NameAndValueVo> getSelectDictDataNormalFilter(@Param("p1") DictConditionVo condition);


    /**
     * 根据字典类型，字典编码，获取字典值
     * @return
     */
    @Select( "                                                                         "
        + "              SELECT                                                        "
        + "                  t1.name, t2.label,t1.code,t2.dict_value,t1.is_del         "
        + "              FROM                                                          "
        + "                  s_dict_type t1                                            "
        + "              	INNER JOIN s_dict_data t2 ON t1.id = t2.dict_type_id       "
        + "              where t1.code = #{p1}                                         "
        + "                and t2.dict_value = #{p2}                                   "
        + "                                                                            ")
    String getDictName(@Param("p1") String code, @Param("p2") String dict_value);
}
